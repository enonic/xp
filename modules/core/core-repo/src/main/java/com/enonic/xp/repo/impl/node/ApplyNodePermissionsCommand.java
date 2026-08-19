package com.enonic.xp.repo.impl.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.node.ApplyNodePermissionsListener;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.Attributes;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.storage.NodeVersionData;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static java.util.Objects.requireNonNullElse;

public class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    /**
     * The stride of the subtree walk: how many nodes are resolved ahead of being applied, bounding both the silent stretch before
     * progress moves and the versions held in memory at once.
     */
    private static final int BATCH_SIZE = 1_000;

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
        this.listener = requireNonNullElse( params.getListener(), NoopApplyNodePermissionsListener.INSTANCE );
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

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.READ, persistedNode );

        final AccessControlList permissions =
            compileNewPermissions( persistedNode.getPermissions(), params.getPermissions(), params.getAddPermissions(),
                                   params.getRemovePermissions() );

        int total = 0;

        if ( ApplyPermissionsScope.SINGLE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            total = applyBatch( List.of( params.getNodeId() ), total, permissions );
        }

        if ( ApplyPermissionsScope.SUBTREE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            String cursor = null;
            do
            {
                // the walk never revisits scanned ground, so one storage refresh up front is enough
                final FindNodeBranchEntriesByParentCommand.Batch batch = FindNodeBranchEntriesByParentCommand.create( this )
                    .parentPath( persistedNode.path() )
                    .requiredPermission( Permission.READ )
                    .batchSize( BATCH_SIZE )
                    .cursor( cursor )
                    .refreshStorage( cursor == null )
                    .build()
                    .executeBatch();

                total = applyBatch( batch.entries().stream().map( NodeBranchEntry::getNodeId ).toList(), total, permissions );

                cursor = batch.cursor();
            }
            while ( cursor != null );
        }

        refresh( RefreshMode.STORAGE );

        return results.build();
    }

    /**
     * Resolves the active versions of one stride of nodes, reports the grown total, and applies the permissions right away, so that
     * progress moves after each stride rather than after the whole subtree has been resolved. Permissions are read and authorized
     * from the reference (context) branch, but applied equally on all specified branches, preserving the caller-supplied branch
     * order, which determines the version origin.
     */
    private int applyBatch( final List<NodeId> nodeIds, final int totalSoFar, final AccessControlList permissions )
    {
        final List<Map<Branch, NodeVersion>> versionsToApply = new ArrayList<>( nodeIds.size() );

        int total = totalSoFar;
        for ( final NodeId nodeId : nodeIds )
        {
            final Map<Branch, NodeVersion> activeNodes = getActiveNodes( nodeId );
            versionsToApply.add( activeNodes );
            total += activeNodes.size();
        }

        if ( total > totalSoFar )
        {
            listener.resolved( total );
            listener.setTotal( total );
        }

        final Branch referenceBranch = ContextAccessor.current().getBranch();

        for ( final Map<Branch, NodeVersion> versionMap : versionsToApply )
        {
            final NodeVersion referenceVersion = versionMap.get( referenceBranch );
            final boolean denied = referenceVersion != null && !allowedOnReferenceBranch( referenceVersion, referenceBranch );

            for ( final Branch branch : branches )
            {
                final NodeVersion version = versionMap.get( branch );
                if ( version != null )
                {
                    doApplyOnNode( version, branch, denied, permissions );
                }
            }
        }

        return total;
    }

    private void doApplyOnNode( final NodeVersion nodeVersion, final Branch branch, final boolean denied,
                                final AccessControlList permissions )
    {
        if ( denied )
        {
            listener.notEnoughRights( 1 );
            results.addResult( nodeVersion.getNodeId(), branch, null, null );
            return;
        }

        NodeHelper.runAsAdmin( () -> {
            final InternalContext adminContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

            final NodePatchCache.Entry<AccessControlList> cachedVersionData = appliedVersions.get( nodeVersion.getNodeVersionId() );

            if ( cachedVersionData != null )
            {
                this.nodeStorageService.push( NodeBranchEntry.fromNodeVersion( cachedVersionData.version() ), adminContext );

                results.addResult( nodeVersion.getNodeId(), branch, cachedVersionData.version(), cachedVersionData.data() );
            }
            else
            {
                final Node originalNode = nodeStorageService.get( nodeVersion.getNodeId(), adminContext );

                final Node editedNode = Node.create( originalNode ).timestamp( Millis.now() ).permissions( permissions ).build();
                final Attributes resolvedAttributes =
                    resolveVersionAttributes( params.getVersionAttributesResolver(), originalNode, editedNode, branch,
                                              nodeVersion.getAttributes() );
                final NodeVersionData result =
                    this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode, resolvedAttributes ), adminContext );
                appliedVersions.put( nodeVersion.getNodeVersionId(), branch, result.version(), result.node().getPermissions() );

                listener.permissionsApplied( 1 );
                results.addResult( nodeVersion.getNodeId(), branch, result.version(), result.node().getPermissions() );
            }
        } );
    }

    private boolean allowedOnReferenceBranch( final NodeVersion nodeVersion, final Branch referenceBranch )
    {
        final InternalContext referenceContext = InternalContext.create( ContextAccessor.current() ).branch( referenceBranch ).build();

        final Node referenceNode = nodeStorageService.get( nodeVersion.getNodeId(), referenceContext );

        return referenceNode != null &&
            NodePermissionsResolver.hasPermission( referenceContext.getPrincipalKeys(), Permission.WRITE_PERMISSIONS,
                                                   referenceNode.getPermissions() );
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
        public void permissionsApplied( final int count )
        {
        }

        @Override
        public void notEnoughRights( final int count )
        {
        }
    }
}
