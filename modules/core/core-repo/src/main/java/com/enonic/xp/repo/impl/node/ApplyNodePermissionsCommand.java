package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.node.ApplyNodePermissionsListener;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

public class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    private final ApplyNodePermissionsParams params;

    private final ApplyPermissionsResult.Builder results;

    private final ApplyNodePermissionsListener listener;

    private final NodePatchCache<AccessControlList> appliedVersions;

    private final Branches branches;

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.results = ApplyPermissionsResult.create();
        this.appliedVersions = new NodePatchCache<>();
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

    public ApplyPermissionsResult execute()
    {
        Preconditions.checkState( this.branches.contains( ContextAccessor.current().getBranch() ),
                                  "Current (source) branch '%s' is not in the list of branches for apply: %s",
                                  ContextAccessor.current().getBranch(), this.branches );

        final Node persistedNode = doGetById( params.getNodeId() );

        if ( persistedNode == null )
        {
            throw new NodeNotFoundException( "Node not found: " + params.getNodeId() );
        }

        final List<Map<Branch, NodeVersion>> versionsToApply = findVersionsToApply();

        if ( listener != NoopApplyNodePermissionsListener.INSTANCE )
        {
            listener.setTotal( versionsToApply.stream().mapToInt( Map::size ).sum() );
        }

        doApply( versionsToApply,
                 compileNewPermissions( persistedNode.getPermissions(), params.getPermissions(), params.getAddPermissions(),
                                        params.getRemovePermissions() ) );

        refresh( RefreshMode.STORAGE );

        return results.build();
    }

    private List<Map<Branch, NodeVersion>> findVersionsToApply()
    {
        final List<Map<Branch, NodeVersion>> result = new ArrayList<>();

        if ( ApplyPermissionsScope.SINGLE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            result.add( getActiveNodes( params.getNodeId() ) );
        }

        if ( ApplyPermissionsScope.SUBTREE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            refresh( RefreshMode.SEARCH );
            result.addAll( findChildrenVersionsToApply(
                this.nodeStorageService.get( params.getNodeId(), InternalContext.from( ContextAccessor.current() ) ).path() ) );
        }

        return result;
    }

    private List<Map<Branch, NodeVersion>> findChildrenVersionsToApply( final NodePath node )
    {
        final List<Map<Branch, NodeVersion>> result = new ArrayList<>();

        final InternalContext internalContext = InternalContext.from( ContextAccessor.current() );

        final SearchResult queryResult = this.nodeSearchService.query(
            NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).withPath( true ).parent( node ).build(),
            SingleRepoSearchSource.from( internalContext ) );

        NodeIds.from( queryResult.getIds() ).stream().map( this::getActiveNodes ).forEach( result::add );

        queryResult.getHits()
            .stream()
            .map(
                hit -> hit.getReturnValues().getOptional( NodeIndexPath.PATH ).map( Object::toString ).map( NodePath::new ).orElse( null ) )
            .filter( Objects::nonNull )
            .forEach( path -> result.addAll( findChildrenVersionsToApply( path ) ) );

        return result;
    }

    private void doApply( List<Map<Branch, NodeVersion>> versionsToApply, final AccessControlList permissions )
    {
        for ( Branch branch : branches )
        {
            versionsToApply.stream()
                .map( versionMap -> versionMap.get( branch ) )
                .filter( Objects::nonNull )
                .forEach( node -> doApplyOnNode( node, branch, permissions ) );
        }
    }

    private void doApplyOnNode( final NodeVersion nodeVersion, final Branch branch, final AccessControlList permissions )
    {
        final InternalContext targetContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

        final Node persistedNode = nodeStorageService.get( nodeVersion.getNodeId(), targetContext );

        if ( persistedNode == null ||
            !NodePermissionsResolver.hasPermission( targetContext.getPrincipalKeys(), Permission.WRITE_PERMISSIONS,
                                                    persistedNode.getPermissions() ) )
        {
            listener.notEnoughRights( 1 );
            results.addResult( nodeVersion.getNodeId(), branch, null, null );
        }

        NodeHelper.runAsAdmin( () -> {
            final NodePatchCache.Entry<AccessControlList> cachedVersionData = appliedVersions.get( nodeVersion.getNodeVersionId() );

            if ( cachedVersionData != null )
            {
                this.nodeStorageService.push( NodeBranchEntry.fromNodeVersion( cachedVersionData.version() ), targetContext );

                results.addResult( nodeVersion.getNodeId(), branch, cachedVersionData.version(), cachedVersionData.data() );
            }
            else
            {
                final Node editedNode = Node.create( persistedNode ).timestamp( Millis.now() ).permissions( permissions ).build();
                final NodeVersionData result =
                    this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode, params.getVersionAttributes() ), targetContext );
                appliedVersions.put( nodeVersion.getNodeVersionId(), branch, result.version(), result.node().getPermissions() );

                listener.permissionsApplied( 1 );
                results.addResult( nodeVersion.getNodeId(), branch, result.version(), result.node().getPermissions() );
            }
        } );
    }

    private Map<Branch, NodeVersion> getActiveNodes( NodeId nodeId )
    {
        return GetActiveNodeVersionsCommand.create()
            .nodeId( nodeId )
            .branches( this.branches )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .build()
            .execute()
            .getNodeVersions();
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
                                    .toList() )
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
