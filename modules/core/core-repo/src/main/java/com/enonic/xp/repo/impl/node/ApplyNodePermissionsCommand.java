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
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
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

        int versions = 0;
        int reported = 0;

        if ( ApplyPermissionsScope.SINGLE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            final NodeBranchEntry rootEntry =
                this.nodeStorageService.getNodeBranchEntry( params.getNodeId(), InternalContext.from( ContextAccessor.current() ) );
            final List<Map<Branch, NodeBranchEntry>> rootEntries = resolveBatch( List.of( rootEntry ) );
            versions += countVersions( rootEntries );
            reported = report( versions, reported );
            applyBatch( rootEntries, permissions );
        }

        if ( ApplyPermissionsScope.SUBTREE == params.getScope() || ApplyPermissionsScope.TREE == params.getScope() )
        {
            int subtreeNodes = 0;
            int subtreeVersions = 0;
            String cursor = null;
            do
            {
                final FindNodeBranchEntriesByParentCommand.Batch batch = FindNodeBranchEntriesByParentCommand.create( this )
                    .parentPath( persistedNode.path() )
                    .requiredPermission( Permission.READ )
                    .batchSize( BATCH_SIZE )
                    .cursor( cursor )
                    .refreshStorage( cursor == null )
                    .build()
                    .executeBatch();

                final List<NodeBranchEntry> stride = batch.entries().stream().toList();
                final List<Map<Branch, NodeBranchEntry>> entriesToApply = resolveBatch( stride );

                final int batchVersions = countVersions( entriesToApply );
                subtreeNodes += stride.size();
                subtreeVersions += batchVersions;
                versions += batchVersions;

                final long ahead = Math.max( 0, batch.totalHits() - BATCH_SIZE );
                final long projectedAhead = subtreeNodes == 0 ? ahead : ahead * subtreeVersions / subtreeNodes;
                reported = report( (int) Math.min( Integer.MAX_VALUE, versions + projectedAhead ), reported );

                applyBatch( entriesToApply, permissions );

                cursor = batch.cursor();
            }
            while ( cursor != null );
        }

        refresh( RefreshMode.STORAGE );

        return results.build();
    }

    private int report( final int total, final int reported )
    {
        if ( total != reported )
        {
            listener.resolved( total );
        }
        return total;
    }

    private static int countVersions( final List<Map<Branch, NodeBranchEntry>> entryMaps )
    {
        return entryMaps.stream().mapToInt( Map::size ).sum();
    }

    private List<Map<Branch, NodeBranchEntry>> resolveBatch( final List<NodeBranchEntry> stride )
    {
        final Branch contextBranch = ContextAccessor.current().getBranch();
        final List<InternalContext> otherBranches = branches.stream()
            .filter( branch -> !branch.equals( contextBranch ) )
            .map( branch -> InternalContext.create( ContextAccessor.current() ).branch( branch ).build() )
            .toList();

        final List<Map<Branch, NodeBranchEntry>> entriesToApply = new ArrayList<>( stride.size() );
        for ( final NodeBranchEntry contextEntry : stride )
        {
            final Map<Branch, NodeBranchEntry> byBranch = new HashMap<>();
            byBranch.put( contextBranch, contextEntry );

            for ( final InternalContext branchContext : otherBranches )
            {
                final NodeBranchEntry entry = this.nodeStorageService.getNodeBranchEntry( contextEntry.getNodeId(), branchContext );
                if ( entry != null )
                {
                    byBranch.put( branchContext.getBranch(), entry );
                }
            }

            entriesToApply.add( byBranch );
        }
        return entriesToApply;
    }

    private void applyBatch( final List<Map<Branch, NodeBranchEntry>> entriesToApply, final AccessControlList permissions )
    {
        final Branch referenceBranch = ContextAccessor.current().getBranch();

        for ( final Map<Branch, NodeBranchEntry> entryMap : entriesToApply )
        {
            final NodeBranchEntry referenceEntry = entryMap.get( referenceBranch );
            final boolean denied = referenceEntry != null && !allowedOnReferenceBranch( referenceEntry, referenceBranch );

            for ( final Branch branch : branches )
            {
                final NodeBranchEntry entry = entryMap.get( branch );
                if ( entry != null )
                {
                    doApplyOnNode( entry, branch, denied, permissions );
                }
            }
        }
    }

    private void doApplyOnNode( final NodeBranchEntry entry, final Branch branch, final boolean denied,
                                final AccessControlList permissions )
    {
        if ( denied )
        {
            listener.notEnoughRights( 1 );
            results.addResult( entry.getNodeId(), branch, null, null );
            return;
        }

        NodeHelper.runAsAdmin( () -> {
            final InternalContext adminContext = InternalContext.create( ContextAccessor.current() ).branch( branch ).build();

            final NodePatchCache.Entry<AccessControlList> cachedVersionData = appliedVersions.get( entry.getVersionId() );

            if ( cachedVersionData != null )
            {
                this.nodeStorageService.push( NodeBranchEntry.fromNodeVersion( cachedVersionData.version() ), adminContext );

                results.addResult( entry.getNodeId(), branch, cachedVersionData.version(), cachedVersionData.data() );
            }
            else
            {
                final Node originalNode =
                    NodeFactory.create( this.nodeStorageService.getNodeVersion( entry.getNodeVersionKey(), adminContext ), entry );

                final Node editedNode = Node.create( originalNode ).timestamp( Millis.now() ).permissions( permissions ).build();

                final Attributes resolvedAttributes = params.getVersionAttributesResolver() == null
                    ? null
                    : resolveVersionAttributes( params.getVersionAttributesResolver(), originalNode, editedNode, branch,
                                                this.nodeStorageService.getVersion( entry.getVersionId(), adminContext )
                                                    .getAttributes() );
                final NodeVersionData result =
                    this.nodeStorageService.store( StoreNodeParams.newVersion( editedNode, resolvedAttributes ), adminContext );
                appliedVersions.put( entry.getVersionId(), branch, result.version(), result.node().getPermissions() );

                listener.permissionsApplied( 1 );
                results.addResult( entry.getNodeId(), branch, result.version(), result.node().getPermissions() );
            }
        } );
    }

    private boolean allowedOnReferenceBranch( final NodeBranchEntry entry, final Branch referenceBranch )
    {
        final InternalContext referenceContext = InternalContext.create( ContextAccessor.current() ).branch( referenceBranch ).build();

        return NodePermissionsResolver.hasPermission( referenceContext.getPrincipalKeys(), Permission.WRITE_PERMISSIONS,
                                                      this.nodeStorageService.getNodePermissions( entry.getNodeVersionKey(),
                                                                                                  referenceContext ) );
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
