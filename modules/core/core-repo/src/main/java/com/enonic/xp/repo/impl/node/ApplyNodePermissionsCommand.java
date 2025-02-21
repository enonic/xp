package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
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

    private final ApplyPermissionsListener listener;

    private final Map<NodeVersionId, NodeVersionMetadata> appliedVersions; // old version id -> new version metadata

    private final Branches branches;

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.results = ApplyNodePermissionsResult.create();
        this.appliedVersions = new HashMap<>();
        this.listener = params.getListener() != null ? params.getListener() : new EmptyApplyPermissionsListener();
        this.branches = params.getBranches().isEmpty() ? Branches.from( ContextAccessor.current().getBranch() ) : params.getBranches();

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
        refresh( RefreshMode.SEARCH );

        doApplyPermissions( params.getNodeId(), params.getPermissions() );

        refresh( RefreshMode.ALL );

        final ApplyNodePermissionsResult result = results.build();
        if ( result.getResults().isEmpty() )
        {
            throw new NodeNotFoundException( "Node not found: " + params.getNodeId() );
        }
        return result;
    }

    private void doApplyPermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        final Node persistedNode =
            ContextBuilder.from( ContextAccessor.current() ).branch( branches.first() ).build().callWith( () -> doGetById( nodeId ) );

        if ( persistedNode == null )
        {
            throw new NodeNotFoundException( "Node not found: " + nodeId );
        }

        if ( ApplyPermissionsScope.CHILDREN == params.getScope() && params.getNodeId().equals( nodeId ) )
        {
            doApplyOnChildren( permissions, persistedNode.path() );
        }
        else
        {
            doApplyOnNode( nodeId, permissions );
        }
    }

    private void doApplyOnNode( final NodeId nodeId, final AccessControlList permissions )
    {
        final Map<Branch, NodeVersionMetadata> activeVersionMap = getActiveNodeVersions( nodeId, branches );

        doApplyOnNode( nodeId, permissions, Branches.from( branches.first() ), activeVersionMap, true );

        final Branches otherBranches = activeVersionMap.keySet()
            .stream()
            .filter( targetBranch -> !targetBranch.equals( branches.first() ) )
            .collect( Branches.collecting() );

        otherBranches.forEach( targetBranch -> doApplyOnNode( nodeId, permissions, otherBranches, activeVersionMap, false ) );
    }

    private void doApplyOnNode( final NodeId nodeId, final AccessControlList permissions, final Branches branches,
                                final Map<Branch, NodeVersionMetadata> activeVersionMap, final boolean recursive )
    {

        branches.forEach( branch -> {
            final NodeVersionData updatedSourceNode = updatePermissionsInBranch( nodeId, appliedVersions.get(
                                                                                     Optional.ofNullable( activeVersionMap.get( branch ) ).map( NodeVersionMetadata::getNodeVersionId ).orElse( null ) ),
                                                                                 permissions, branch );

            if ( updatedSourceNode != null )
            {
                appliedVersions.put( activeVersionMap.get( branch ).getNodeVersionId(), updatedSourceNode.nodeVersionMetadata() );
            }

            results.addResult( nodeId, branch, updatedSourceNode != null ? updatedSourceNode.node() : null );

            if ( recursive )
            {
                if ( updatedSourceNode != null && updatedSourceNode.node() != null )
                {
                    doApplyOnChildren( permissions, updatedSourceNode.node().path() );
                }
            }
        } );
    }

    private void doApplyOnChildren( final AccessControlList permissions, final NodePath parentPath )
    {
        final Context sourceBranchContext = ContextBuilder.from( ContextAccessor.current() ).branch( branches.first() ).build();

        final NodeIds childrenIds = NodeIds.from(
            this.nodeSearchService.query( NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).parent( parentPath ).build(),
                                          SingleRepoSearchSource.from( sourceBranchContext ) ).getIds() );

        final Nodes children = this.nodeStorageService.get( childrenIds, InternalContext.from( sourceBranchContext ) );

        for ( Node child : children )
        {
            final PermissionsMergingStrategy mergingStrategy =
                ApplyPermissionsScope.SINGLE == params.getScope() ? PermissionsMergingStrategy.MERGE : PermissionsMergingStrategy.OVERWRITE;

            final AccessControlList childPermissions = mergingStrategy.mergePermissions( child.getPermissions(), permissions );

            doApplyOnNode( child.id(), childPermissions );
        }
    }

    private NodeVersionData updatePermissionsInBranch( final NodeId nodeId, final NodeVersionMetadata updatedVersionMetadata,
                                                       final AccessControlList permissions, final Branch branch )
    {
        final InternalContext targetContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        final Node persistedNode = nodeStorageService.get( nodeId, targetContext );

        if ( persistedNode == null ||
            !NodePermissionsResolver.hasPermission( targetContext.getPrincipalsKeys(), Permission.WRITE_PERMISSIONS,
                                                    persistedNode.getPermissions() ) )
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

            return new NodeVersionData( nodeStorageService.get( updatedVersionMetadata.getNodeVersionId(), targetContext ),
                                        updatedVersionMetadata );
        }
        else
        {
            final Node editedNode = Node.create( persistedNode )
                .timestamp( Instant.now( CLOCK ) )
                .permissions( compileNewPermissions( persistedNode.getPermissions(), permissions, params.getAddPermissions(),
                                                     params.getRemovePermissions() ) )
                .build();
            result = this.nodeStorageService.store( StoreNodeParams.create().node( editedNode ).build(), targetContext );
        }

        listener.permissionsApplied( 1 );
        return result;
    }

    private Map<Branch, NodeVersionMetadata> getActiveNodeVersions( final NodeId nodeId, final Branches branches )
    {

        return GetActiveNodeVersionsCommand.create( this ).nodeId( nodeId ).branches( branches ).build().execute().getNodeVersions();
    }

    private AccessControlList compileNewPermissions( final AccessControlList persistedPermissions, final AccessControlList permissions,
                                                     final AccessControlList addPermissions, final AccessControlList removePermissions )
    {

        if ( !permissions.isEmpty() )
        {
            return permissions;
        }

        final HashMap<PrincipalKey, AccessControlEntry> newPermissions = new HashMap<>( persistedPermissions.asMap() );

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
                if ( entryToRemove.allowedPermissions().isEmpty() ) //remove all if no permissions specified
                {
                    newPermissions.remove( entryToRemove.getPrincipal() );
                }
                else
                {
                    newPermissions.put( entryToRemove.getPrincipal(), AccessControlEntry.create()
                        .principal( entryToRemove.getPrincipal() )
                        .allow( currentACE.allowedPermissions()
                                    .stream()
                                    .filter( permission -> !entryToRemove.allowedPermissions().contains( permission ) )
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
