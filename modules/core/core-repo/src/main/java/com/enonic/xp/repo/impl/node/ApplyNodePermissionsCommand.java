package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

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

    private void verifyBranch()
    {
        Preconditions.checkState( this.branches.contains( ContextAccessor.current().getBranch() ),
                                  "Current(source) branch '%s' is not in the list of branches for apply: %s",
                                  ContextAccessor.current().getBranch(), this.branches );
    }

    public ApplyNodePermissionsResult execute()
    {
        final Context context = this.branches.getSize() == 1
            ? ContextBuilder.from( ContextAccessor.current() ).branch( this.branches.first() ).build()
            : ContextAccessor.current();

        context.runWith( () -> {
            verifyBranch();
            doApplyPermissions();
        } );

        return results.build();
    }

    private void doApplyPermissions()
    {
        refresh( RefreshMode.SEARCH );

        final Node persistedNode =
            ContextBuilder.from( ContextAccessor.current() ).build().callWith( () -> doGetById( params.getNodeId() ) );

        if ( persistedNode == null )
        {
            throw new NodeNotFoundException( "Node not found: " + params.getNodeId() );
        }

        final List<Map<Branch, Node>> versionsToApply = findVersionsToApply( persistedNode );

        doApply( versionsToApply );

        refresh( RefreshMode.STORAGE );
    }

    private List<Map<Branch, Node>> findVersionsToApply( final Node node )
    {
        final List<Map<Branch, Node>> result = new ArrayList<>();

        if ( ApplyPermissionsScope.SINGLE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            result.add( getActiveNodes( params.getNodeId(), this.branches ) );
        }

        final Context sourceBranchContext = ContextBuilder.from( ContextAccessor.current() ).build();

        result.addAll(
            findChildrenVersionsToApply( this.nodeStorageService.get( params.getNodeId(), InternalContext.from( sourceBranchContext ) ) ) );

        return result;
    }

    private List<Map<Branch, Node>> findChildrenVersionsToApply( final Node node )
    {
        final List<Map<Branch, Node>> result = new ArrayList<>();

        final Context sourceBranchContext = ContextBuilder.from( ContextAccessor.current() ).build();

        final NodeIds childrenIds = NodeIds.from(
            this.nodeSearchService.query( NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).parent( node.path() ).build(),
                                          SingleRepoSearchSource.from( sourceBranchContext ) ).getIds() );

        final Nodes children = this.nodeStorageService.get( childrenIds, InternalContext.from( sourceBranchContext ) );

        children.stream().map( child -> getActiveNodes( child.id(), this.branches ) ).forEach( result::add );

        children.stream().map( this::findChildrenVersionsToApply ).forEach( result::addAll );

        return result;
    }


    private void doApply( List<Map<Branch, Node>> versionsToApply )
    {

        branches.forEach( branch -> {
            versionsToApply.stream()
                .map( versionMap -> versionMap.get( branch ) )
                .forEach( versionMetadata -> doApplyOnNode( versionMetadata, branch ) );
        } );
    }

    private void doApplyOnNode( final Node node, final Branch branch )
    {
        if ( node == null )
        {
            return;
        }

        final PermissionsMergingStrategy mergingStrategy =
            !params.getNodeId().equals( node.id() ) && ApplyPermissionsScope.SINGLE == params.getScope()
                ? PermissionsMergingStrategy.MERGE
                : PermissionsMergingStrategy.OVERWRITE;

        final AccessControlList permissions = mergingStrategy.mergePermissions( node.getPermissions(), params.getPermissions() );

        final NodeVersionData updatedSourceNode =
            updatePermissionsInBranch( node.id(), appliedVersions.get( node.getNodeVersionId() ), branch, permissions );

        if ( updatedSourceNode != null )
        {
            appliedVersions.put( node.getNodeVersionId(), updatedSourceNode.nodeVersionMetadata() );
        }

        results.addResult( node.id(), branch, updatedSourceNode != null ? updatedSourceNode.node() : null );
    }

    private NodeVersionData updatePermissionsInBranch( final NodeId nodeId, final NodeVersionMetadata updatedVersionMetadata,
                                                       final Branch branch, final AccessControlList permissions )
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

        return NodeHelper.runAsAdmin( () -> {
            NodeVersionData result;
            if ( updatedVersionMetadata != null )
            {
                this.nodeStorageService.push( List.of( PushNodeEntry.create()
                                                           .nodeBranchEntry( NodeBranchEntry.create()
                                                                                 .nodeVersionId( updatedVersionMetadata.getNodeVersionId() )
                                                                                 .nodePath( updatedVersionMetadata.getNodePath() )
                                                                                 .nodeVersionKey(
                                                                                     updatedVersionMetadata.getNodeVersionKey() )
                                                                                 .nodeId( updatedVersionMetadata.getNodeId() )
                                                                                 .timestamp( updatedVersionMetadata.getTimestamp() )
                                                                                 .build() )
                                                           .build() ), branch, l -> {
                }, targetContext );

                return new NodeVersionData( nodeStorageService.get( updatedVersionMetadata.getNodeVersionId(),
                                                                    InternalContext.create( ContextAccessor.current() ).build() ),
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
        } );
    }

    private Map<Branch, Node> getActiveNodes( final NodeId nodeId, final Branches branches )
    {
        final Map<Branch, Node> result = new HashMap<>();

        branches.forEach( branch -> {
            InternalContext context = InternalContext.from( ContextBuilder.copyOf( ContextAccessor.current() ).branch( branch ).build() );
            final Node node = nodeStorageService.get( nodeId, context );
            result.put( branch, node );
        } );

        return result;
    }

    private AccessControlList compileNewPermissions( final AccessControlList persistedPermissions, final AccessControlList permissions,
                                                     final AccessControlList addPermissions, final AccessControlList removePermissions )
    {
        if ( !permissions.isEmpty() )
        {
            return permissions;
        }
        else if ( addPermissions.isEmpty() && removePermissions.isEmpty() )
        {
            return AccessControlList.empty();
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
