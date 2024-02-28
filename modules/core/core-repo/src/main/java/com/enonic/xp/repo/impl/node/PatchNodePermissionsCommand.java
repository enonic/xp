package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.PatchPermissionsParams;
import com.enonic.xp.node.PatchPermissionsResult;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.StoreNodeCommitParams;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class PatchNodePermissionsCommand
    extends AbstractNodeCommand
{
    private final PatchPermissionsParams params;

    private final List<PatchPermissionsResult> results;

    private final NodeCommitId nodeCommitId;

    private final Branch sourceBranch;

    private final Branches allBranches;

    private PatchNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.results = new ArrayList<>();
        this.sourceBranch = ContextAccessor.current().getBranch();
        this.allBranches = builder.repositoryService.get( ContextAccessor.current().getRepositoryId() ).getBranches();
        this.nodeCommitId = new NodeCommitId();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<PatchPermissionsResult> execute()
    {
        doPatchPermissions( params.getNodeId(), params.getPermissions() );

        if ( results.stream().anyMatch( result -> result.getBranchResult().get( ContentConstants.BRANCH_MASTER ) != null ) )
        {
            storeCommit();
        }

        return results;
    }

    private void doPatchPermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        final PatchPermissionsResult.Builder result = PatchPermissionsResult.create();
        result.nodeId( nodeId );

        final Map<Branch, NodeVersionMetadata> activeVersionMap = getActiveNodeVersions( nodeId );

        final Node updatedNode = updatePermissionsInBranch( nodeId, null, permissions, this.sourceBranch );

        if ( updatedNode == null )
        {
            result.branchResult( this.sourceBranch, null );
            return;
        }

        result.branchResult( this.sourceBranch, updatedNode.getNodeVersionId() );

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
            result.branchResult( targetBranch, updatedTargetNode != null ? updatedTargetNode.getNodeVersionId() : null );

        } );

        results.add( result.build() );

        if ( params.isOverwriteChildrenPermissions() )
        {
            final NodeIds childrenIds = NodeIds.from( this.nodeSearchService.query(
                    NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).parent( updatedNode.path() ).build(),
                    SingleRepoSearchSource.from( ContextBuilder.from( ContextAccessor.current() ).branch( this.sourceBranch ).build() ) )
                                                          .getIds() );

            for ( NodeId child : childrenIds )
            {
                doPatchPermissions( child, permissions );
            }
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

        return this.nodeStorageService.store( nodeBuilder.build(), targetContext );
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
        return GetActiveNodeVersionsCommand.create( this )
            .nodeId( nodeId )
            .branches( this.allBranches )
            .build()
            .execute()
            .getNodeVersions();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private PatchPermissionsParams params;

        private RepositoryService repositoryService;

        private Builder()
        {
        }

        public Builder params( final PatchPermissionsParams val )
        {
            params = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return this;
        }

        public PatchNodePermissionsCommand build()
        {
            return new PatchNodePermissionsCommand( this );
        }
    }
}
