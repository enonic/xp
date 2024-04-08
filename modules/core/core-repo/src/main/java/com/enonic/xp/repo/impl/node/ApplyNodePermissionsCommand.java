package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

public class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    private final ApplyNodePermissionsParams params;

    private final ApplyNodePermissionsResult.Builder results;

    private final Branch sourceBranch;

    private final ApplyPermissionsListener listener;

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.results = ApplyNodePermissionsResult.create();
        this.sourceBranch = ContextAccessor.current().getBranch();
        listener = params.getListener() != null ? params.getListener() : new EmptyApplyPermissionsListener();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public ApplyNodePermissionsResult execute()
    {
        if ( params.getBranches().contains( this.sourceBranch ) )
        {
            throw new IllegalArgumentException( "Branches cannot contain current branch" );
        }

        final Node persistedNode = doGetById( params.getNodeId() );

        if ( persistedNode == null )
        {
            throw new NodeNotFoundException(
                String.format( "Node with id [%s] not found in branch [%s]", params.getNodeId(), sourceBranch ) );
        }

        AccessControlList permissions = params.getPermissions();

        if ( params.getPermissions().isEmpty() && params.getAddPermissions().isEmpty() && params.getRemovePermissions().isEmpty() )
        {
            permissions = persistedNode.getPermissions();
        }

        refresh( RefreshMode.SEARCH );

        doApplyPermissions( params.getNodeId(), permissions );

        refresh( RefreshMode.ALL );

        return results.build();
    }

    private void doApplyPermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        final Map<Branch, NodeVersionMetadata> activeVersionMap = getActiveNodeVersions( nodeId );

        final NodeVersionData updatedOriginNode = updatePermissionsInBranch( nodeId, null, permissions, this.sourceBranch );

        if ( updatedOriginNode == null )
        {
            results.addBranchResult( nodeId, this.sourceBranch, null );
            return;
        }

        results.addBranchResult( nodeId, this.sourceBranch, updatedOriginNode.node() );

        activeVersionMap.keySet().forEach( targetBranch -> {
            if ( targetBranch.equals( this.sourceBranch ) )
            {
                return;
            }

            final boolean isEqualToOrigin =
                activeVersionMap.get( targetBranch ).getNodeVersionId().equals( activeVersionMap.get( sourceBranch ).getNodeVersionId() );

            final NodeVersionData updatedTargetNode =
                updatePermissionsInBranch( nodeId, isEqualToOrigin ? updatedOriginNode.nodeVersionMetadata() : null, permissions,
                                           targetBranch );
            ;
            results.addBranchResult( nodeId, targetBranch, isEqualToOrigin
                ? updatedOriginNode.node()
                : updatedTargetNode != null ? updatedTargetNode.node() : null );

        } );

        final NodeIds childrenIds = NodeIds.from( this.nodeSearchService.query(
                NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).parent( updatedOriginNode.node().path() ).build(),
                SingleRepoSearchSource.from( ContextBuilder.from( ContextAccessor.current() ).branch( this.sourceBranch ).build() ) )
                                                      .getIds() );

        final Nodes children = this.nodeStorageService.get( childrenIds, InternalContext.from( ContextAccessor.current() ) );

        for ( Node child : children )
        {
            final PermissionsMergingStrategy mergingStrategy =
                params.isOverwriteChildPermissions() ? PermissionsMergingStrategy.OVERWRITE : PermissionsMergingStrategy.DEFAULT;

            final AccessControlList childPermissions = mergingStrategy.mergePermissions( child.getPermissions(), permissions );

            doApplyPermissions( child.id(), childPermissions );
        }
    }

    private NodeVersionData updatePermissionsInBranch( final NodeId nodeId, final NodeVersionMetadata updatedVersionMetadata,
                                                       final AccessControlList permissions, final Branch branch )
    {
        final InternalContext targetContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        final Node persistedNode = nodeStorageService.get( nodeId, targetContext );

        if ( persistedNode == null ||
            !NodePermissionsResolver.contextUserHasPermissionOrAdmin( Permission.WRITE_PERMISSIONS, persistedNode.getPermissions() ) )
        {
            listener.notEnoughRights( 1 );
            return null;
        }

        NodeVersionData result;

        if ( updatedVersionMetadata != null )
        {
            this.nodeStorageService.push( List.of( PushNodeEntry.create()
                                                       .nodeBranchEntry( NodeBranchEntry.create()
                                                                             .nodeVersionId( updatedVersionMetadata.getNodeVersionId() )
                                                                             .nodePath( updatedVersionMetadata.getNodePath() )
                                                                             .nodeVersionKey( updatedVersionMetadata.getNodeVersionKey() )
                                                                             .nodeId( updatedVersionMetadata.getNodeId() )
                                                                             .timestamp( updatedVersionMetadata.getTimestamp() )
                                                                             .build() )
                                                       .build() ), branch, l -> {
            }, InternalContext.from( ContextAccessor.current() ) );
            return null;
        }
        else
        {
            final Node editedNode = Node.create( persistedNode )
                .timestamp( Instant.now( CLOCK ) )
                .permissions(
                    compileNewPermissions( persistedNode, permissions, params.getAddPermissions(), params.getRemovePermissions() ) )
                .build();
            result = this.nodeStorageService.store( StoreNodeParams.create().node( editedNode ).build(), targetContext );
        }

        listener.permissionsApplied( 1 );
        return result;
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

    private AccessControlList compileNewPermissions( final Node persistedNode, final AccessControlList permissions,
                                                     final AccessControlList addPermissions, final AccessControlList removePermissions )
    {

        if ( !permissions.isEmpty() )
        {
            return permissions;
        }

        final HashMap<PrincipalKey, AccessControlEntry> newPermissions = new HashMap<>( persistedNode.getPermissions().getMap() );

        if ( !addPermissions.isEmpty() )
        {
            addPermissions.getEntries().forEach( entryToAdd -> {
                newPermissions.compute( entryToAdd.getPrincipal(), ( key, entry ) -> entry == null
                    ? entryToAdd
                    : AccessControlEntry.create()
                        .principal( entry.getPrincipal() )
                        .allow( entry.getAllowedPermissions() )
                        .allow( entryToAdd.getAllowedPermissions() )
                        .deny( entry.getDeniedPermissions() )
                        .deny( entryToAdd.getDeniedPermissions() )
                        .build() );
            } );
        }

        if ( !removePermissions.isEmpty() )
        {
            removePermissions.getEntries().forEach( entryToRemove -> {
                final AccessControlEntry currentACE = newPermissions.get( entryToRemove.getPrincipal() );
                if ( currentACE == null )
                {
                    return;
                }
                if ( entryToRemove.getAllowedPermissions().isEmpty() ) //remove all if no permissions specified
                {
                    newPermissions.remove( entryToRemove.getPrincipal() );
                }
                else
                {
                    newPermissions.put( entryToRemove.getPrincipal(), AccessControlEntry.create()
                        .principal( entryToRemove.getPrincipal() )
                        .allow( currentACE.getAllowedPermissions()
                                    .stream()
                                    .filter( permission -> !entryToRemove.getAllowedPermissions().contains( permission ) )
                                    .collect( Collectors.toList() ) )
                        .build() );
                }
            } );
        }

        return AccessControlList.create().addAll( newPermissions.values() ).build();
    }

    private static class EmptyApplyPermissionsListener
        implements ApplyPermissionsListener
    {
        @Override
        public void permissionsApplied( final int count )
        {
        }

        @Override
        public void notEnoughRights( final int count )
        {
        }

        @Override
        public void setTotal( final int count )
        {
        }
    }


    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private ApplyNodePermissionsParams params;

        private Builder()
        {
        }

        private Builder( AbstractNodeCommand source )
        {
            super( source );
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
