package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.StoreNodeCommitParams;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    private final ApplyNodePermissionsParams params;

    private final ApplyNodePermissionsResult.Builder results;

    private final NodeCommitId nodeCommitId;

    private final Branch sourceBranch;

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.results = ApplyNodePermissionsResult.create();
        this.sourceBranch = ContextAccessor.current().getBranch();
        this.nodeCommitId = new NodeCommitId();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplyNodePermissionsResult execute()
    {
        final Node persistedNode = nodeStorageService.get( params.getNodeId(), InternalContext.from( ContextAccessor.current() ) );

        if ( persistedNode == null )
        {
            throw new NodeNotFoundException(
                String.format( "Node with id [%s] not found in branch [%s]", params.getNodeId(), sourceBranch ) );
        }

        AccessControlList permissions = params.getPermissions();

        if ( permissions == null || permissions.isEmpty() )
        {
            permissions = persistedNode.getPermissions();
        }

        doPatchPermissions( params.getNodeId(), permissions );

        final ApplyNodePermissionsResult result = results.build();

        if ( result.getBranchResults()
            .values()
            .stream().anyMatch( l -> ContentConstants.BRANCH_MASTER.equals( l.get( 0 ).getBranch() ) && l.get( 0 ).getNode() != null ) )
        {
            storeCommit();
        }

        return result;
    }

    private void doPatchPermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        final Map<Branch, NodeVersionMetadata> activeVersionMap = getActiveNodeVersions( nodeId );

        final Node updatedNode = updatePermissionsInBranch( nodeId, null, permissions, this.sourceBranch );

        if ( updatedNode == null )
        {
            results.addBranchResult( nodeId, this.sourceBranch, null );
            return;
        }

        results.addBranchResult( nodeId, this.sourceBranch, updatedNode );

        activeVersionMap.keySet().forEach( targetBranch -> {
            if ( targetBranch.equals( this.sourceBranch ) )
            {
                return;
            }

            final boolean isEqualToOrigin =
                activeVersionMap.get( targetBranch ).getNodeVersionId().equals( activeVersionMap.get( sourceBranch ).getNodeVersionId() );

            final Node updatedTargetNode =
                updatePermissionsInBranch( nodeId, isEqualToOrigin ? updatedNode.getNodeVersionId() : null, permissions, targetBranch );
            ;
            results.addBranchResult( nodeId, targetBranch, updatedTargetNode );

        } );

        final NodeIds childrenIds = NodeIds.from( this.nodeSearchService.query(
                NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).parent( updatedNode.path() ).build(),
                SingleRepoSearchSource.from( ContextBuilder.from( ContextAccessor.current() ).branch( this.sourceBranch ).build() ) )
                                                      .getIds() );

        final Nodes children = this.nodeStorageService.get( childrenIds, InternalContext.from( ContextAccessor.current() ) );

        for ( Node child : children )
        {
            final PermissionsMergingStrategy mergingStrategy =
                params.isOverwriteChildPermissions() ? PermissionsMergingStrategy.OVERWRITE : PermissionsMergingStrategy.DEFAULT;

            final AccessControlList childPermissions = mergingStrategy.mergePermissions( child.getPermissions(), permissions );

            doPatchPermissions( child.id(), childPermissions );
        }

        if ( params.isOverwriteChildPermissions() )
        {

        }
    }

    private Node updatePermissionsInBranch( final NodeId nodeId, final NodeVersionId nodeVersionId, final AccessControlList permissions,
                                            final Branch branch )
    {
        final InternalContext targetContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        final Node persistedNode = nodeStorageService.get( nodeId, targetContext );

        if ( persistedNode == null ||
            !NodePermissionsResolver.contextUserHasPermissionOrAdmin( Permission.WRITE_PERMISSIONS, persistedNode.getPermissions() ) )
        {
            if ( params.getListener() != null )
            {
                params.getListener().notEnoughRights( 1 );
            }

            return null;
        }

        final Node editedNode = Node.create( persistedNode )
            .timestamp( Instant.now( CLOCK ) )
            .permissions( permissions )
            .nodeVersionId( nodeVersionId )
            .build();

        final StoreNodeParams.Builder nodeBuilder = StoreNodeParams.create().node( editedNode );

        if ( nodeVersionId != null )
        {
            nodeBuilder.overrideVersion();
        }

        if ( branch.equals( ContentConstants.BRANCH_MASTER ) )
        {
            nodeBuilder.nodeCommitId( this.nodeCommitId );
        }

        final Node result = this.nodeStorageService.store( nodeBuilder.build(), targetContext );

        if ( params.getListener() != null )
        {
            params.getListener().permissionsApplied( 1 );
        }

        return result;
    }

    private void storeCommit()
    {
        nodeStorageService.storeCommit( StoreNodeCommitParams.create()
                                            .nodeCommitId( this.nodeCommitId )
                                            .timestamp( Instant.now( CLOCK ) )
                                            .message( "Patch permissions" )
                                            .build(), InternalContext.from( ContextAccessor.current() ) );
    }

    private Map<Branch, NodeVersionMetadata> getActiveNodeVersions( final NodeId nodeId )
    {
        return params.getBranches() != null ? GetActiveNodeVersionsCommand.create( this )
            .nodeId( nodeId )
            .branches( Streams.concat( params.getBranches().stream(), Stream.of( this.sourceBranch ) ).collect( Branches.collecting() ) )
            .build()
            .execute()
            .getNodeVersions() : Map.of();
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private ApplyNodePermissionsParams params;

        private Builder()
        {
        }

        public Builder params( final ApplyNodePermissionsParams val )
        {
            params = val;
            return this;
        }

        public ApplyNodePermissionsCommand build()
        {
            return new ApplyNodePermissionsCommand( this );
        }
    }
}
