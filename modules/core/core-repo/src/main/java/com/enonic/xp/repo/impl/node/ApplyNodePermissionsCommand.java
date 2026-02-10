package com.enonic.xp.repo.impl.node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsListener;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
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

    private final ApplyNodePermissionsListener listener;

    private final Map<NodeVersionId, NodeVersionData> appliedVersions; // old version id -> new version data

    private final Branches branches;

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.results = ApplyNodePermissionsResult.create();
        this.appliedVersions = new HashMap<>();
        this.listener = Objects.requireNonNullElse( params.getListener(), NoopApplyNodePermissionsListener.INSTANCE );
        this.branches = params.getBranches().isEmpty() ? Branches.from( ContextAccessor.current().getBranch() ) : params.getBranches();
    }

    public static Builder create()
    {
        return new Builder();
    }

    static Builder create( AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    private void verifyBranch()
    {
        Preconditions.checkState( this.branches.contains( ContextAccessor.current().getBranch() ),
                                  "Current (source) branch '%s' is not in the list of branches for apply: %s",
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

        final Node persistedNode = doGetById( params.getNodeId() );

        if ( persistedNode == null )
        {
            throw new NodeNotFoundException( "Node not found: " + params.getNodeId() );
        }

        final List<Map<Branch, Node>> versionsToApply = findVersionsToApply();

        if ( listener != NoopApplyNodePermissionsListener.INSTANCE )
        {
            listener.setTotal( versionsToApply.stream().mapToInt( Map::size ).sum() );
        }

        doApply( versionsToApply,
                 compileNewPermissions( persistedNode.getPermissions(), params.getPermissions(), params.getAddPermissions(),
                                        params.getRemovePermissions() ) );

        refresh( RefreshMode.STORAGE );
    }

    private List<Map<Branch, Node>> findVersionsToApply()
    {
        final List<Map<Branch, Node>> result = new ArrayList<>();

        if ( ApplyPermissionsScope.SINGLE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            result.add( getActiveNodes( params.getNodeId(), this.branches ) );
        }

        final Context sourceBranchContext = ContextBuilder.from( ContextAccessor.current() ).build();

        if ( ApplyPermissionsScope.SUBTREE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            result.addAll( findChildrenVersionsToApply(
                this.nodeStorageService.get( params.getNodeId(), InternalContext.from( sourceBranchContext ) ) ) );
        }

        return result;
    }

    private List<Map<Branch, Node>> findChildrenVersionsToApply( final Node node )
    {
        final List<Map<Branch, Node>> result = new ArrayList<>();

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        final NodeIds childrenIds = NodeIds.from(
            this.nodeSearchService.query( NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).parent( node.path() ).build(),
                                          SingleRepoSearchSource.from(  internalContext ) ).getIds() );

        final Nodes children = this.nodeStorageService.get( childrenIds, internalContext );

        children.stream().map( child -> getActiveNodes( child.id(), this.branches ) ).forEach( result::add );

        children.stream().map( this::findChildrenVersionsToApply ).forEach( result::addAll );

        return result;
    }

    private void doApply( List<Map<Branch, Node>> versionsToApply, final AccessControlList permissions )
    {
        for ( Branch branch : branches )
        {
            versionsToApply.stream()
                .map( versionMap -> versionMap.get( branch ) )
                .forEach( node -> doApplyOnNode( node, branch, permissions ) );
        }
    }

    private void doApplyOnNode( final Node node, final Branch branch, final AccessControlList permissions )
    {
        if ( node == null )
        {
            return;
        }

        final NodeVersionData updatedSourceNode =
            updatePermissionsInBranch( node.id(), appliedVersions.get( node.getNodeVersionId() ), branch, permissions );

        if ( updatedSourceNode != null )
        {
            appliedVersions.put( node.getNodeVersionId(), updatedSourceNode );
        }

        results.addResult( node.id(), branch, updatedSourceNode != null ? updatedSourceNode.metadata().getNodeVersionId() : null,
                           updatedSourceNode != null ? updatedSourceNode.node().getPermissions() : null );
    }

    private NodeVersionData updatePermissionsInBranch( final NodeId nodeId, final NodeVersionData updatedVersionData,
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
            if ( updatedVersionData != null )
            {
                this.nodeStorageService.push( NodeBranchEntry.fromNodeVersion( updatedVersionData.metadata() ), this.branches.first(), targetContext );
                return updatedVersionData;
            }
            else
            {
                final Node editedNode = Node.create( persistedNode ).timestamp( Instant.now( CLOCK ) ).permissions( permissions )
                    .build();
                final NodeVersionData result = this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode, params.getVersionAttributes() ), targetContext );

                listener.permissionsApplied( 1 );
                return result;
            }
        } );
    }

    private Map<Branch, Node> getActiveNodes( final NodeId nodeId, final Branches branches )
    {
        final Map<Branch, Node> result = new HashMap<>();

        for ( Branch branch : branches )
        {
            final Node node =
                nodeStorageService.get( nodeId, InternalContext.create( ContextAccessor.current() ).branch( branch ).build() );
            result.put( branch, node );
        }

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
            for ( AccessControlEntry entryToAdd : addPermissions.getEntries() )
            {
                newPermissions.compute( entryToAdd.getPrincipal(), ( key, entry ) -> entry == null
                    ? entryToAdd
                    : AccessControlEntry.create()
                        .principal( entry.getPrincipal() )
                        .allow( entry.getAllowedPermissions() )
                        .allow( entryToAdd.getAllowedPermissions() )
                        .deny( entry.getDeniedPermissions() )
                        .deny( entryToAdd.getDeniedPermissions() )
                        .build() );
            }
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

    private enum NoopApplyNodePermissionsListener
        implements ApplyNodePermissionsListener
    {
        INSTANCE;

        @Override
        public void setTotal( final int count )
        {
        }

        @Override
        public void permissionsApplied( final int count )
        {
        }

        @Override
        public void notEnoughRights( final int count )
        {
        }
    }
}
