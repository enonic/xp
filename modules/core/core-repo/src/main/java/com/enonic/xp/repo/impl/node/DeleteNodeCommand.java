package com.enonic.xp.repo.impl.node;

import java.util.List;
import com.google.common.collect.Iterables;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.DeleteNodeListener;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchVersionFactory;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.storage.spi.NodeStore;

import static java.util.Objects.requireNonNullElse;

public class DeleteNodeCommand
    extends AbstractNodeCommand
{
    private static final int BATCH_SIZE = 20;

    private final DeleteNodeListener deleteNodeListener;

    private final NodeId nodeId;

    private final NodePath nodePath;

    private final RefreshMode refresh;

    /**
     * The storage SPI, for the branch-entry listing surface (Phase 4 decision D2). Nullable: the
     * default/ES path never needs it, and a null store simply keeps the legacy
     * {@code NodeBranchQuery} flow.
     */
    private final NodeStore nodeStore;

    private DeleteNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.nodeId;
        this.nodePath = builder.nodePath;
        this.nodeStore = builder.nodeStore;
        this.deleteNodeListener = requireNonNullElse( builder.deleteNodeListener, _ -> {
        } );
        this.refresh = builder.refresh;
    }

    public NodeBranchEntries execute()
    {
        if ( NodeId.ROOT.equals( this.nodeId ) || NodePath.ROOT.equals( this.nodePath ) )
        {
            throw new OperationNotPermittedException( "Not allowed to delete root-node" );
        }

        final Context context = ContextAccessor.current();
        final InternalContext internalContext = InternalContext.from( context );
        final AuthenticationInfo authInfo = context.getAuthInfo();

        final NodeBranchEntry node = nodeId != null
            ? this.nodeStorageService.getNodeBranchEntry( nodeId, internalContext )
            : this.nodeStorageService.getNodeBranchEntry( nodePath, internalContext );

        if ( node == null )
        {
            // Node not found in storage, but potentially still in index. Attempt to fixup.
            if ( nodeId != null )
            {
                this.nodeStorageService.deleteFromIndex( nodeId, internalContext );
            }
            return NodeBranchEntries.empty();
        }

        final NodePath effectiveNodePath = node.getNodePath();

        final NodeBranchEntries childrenBranchEntries = listChildren( internalContext, effectiveNodePath );

        final NodeBranchEntries nodeBranchEntries = NodeBranchEntries.create().addAll( childrenBranchEntries ).add( node ).build();

        if ( !authInfo.hasRole( RoleKeys.ADMIN ) )
        {
            for ( NodeBranchEntry branchEntry : nodeBranchEntries )
            {
                final AccessControlList nodePermissions =
                    this.nodeStorageService.getNodePermissions( branchEntry.getNodeVersionKey(), internalContext );

                if ( !NodePermissionsResolver.hasPermission( internalContext.getPrincipalKeys(), Permission.DELETE, nodePermissions ) )
                {
                    throw new NodeAccessException( authInfo.getUser(), effectiveNodePath, Permission.DELETE );
                }
            }
        }

        for ( final List<NodeBranchEntry> batch : Iterables.partition( nodeBranchEntries, BATCH_SIZE ) )
        {
            this.nodeStorageService.delete( batch, internalContext );
            this.deleteNodeListener.nodesDeleted( batch.size() );
        }

        refresh( refresh );

        return nodeBranchEntries;
    }

    /**
     * The delete cascade's subtree listing.
     * <p>
     * Phase 4 decision D2 (nodb/BUILD-PHASE-4.md): this used to be a {@code NodeBranchQuery}
     * against the ES {@code storage-<repo>} index — the last such consumer, and the reason a
     * non-ES backend could not delete a node with children at all. A backend that can answer it
     * from its own system of record does so; the {@code NodeBranchQuery} below is unchanged for
     * every backend that cannot.
     * <p>
     * Note the {@code refresh(STORAGE)} on the legacy path only. It exists because the ES storage
     * index is a near-real-time index and a stale one would silently return an INCOMPLETE subtree
     * to a destructive operation. Against a transactional store the read is already consistent, so
     * forcing a refresh there would be pure cost — and against an asynchronously indexed search
     * backend it would have had to become a wait on indexer lag, which is precisely the shape D2
     * exists to avoid.
     */
    private NodeBranchEntries listChildren( final InternalContext internalContext, final NodePath effectiveNodePath )
    {
        if ( this.nodeStore != null && this.nodeStore.supportsBranchEntryQueries() )
        {
            return NodeBranchVersionFactory.fromListing(
                this.nodeStore.listChildEntries( internalContext.getRepositoryId(), internalContext.getBranch(),
                                                  effectiveNodePath.toString() ) );
        }

        refresh( RefreshMode.STORAGE );

        return NodeBranchQueryResultFactory.create( this.nodeSearchService.query( NodeBranchQuery.create()
                                                                                     .query( QueryExpr.from( CompareExpr.like(
                                                                                         FieldExpr.from( BranchIndexPath.PATH ),
                                                                                         ValueExpr.string(
                                                                                             effectiveNodePath + "/*" ) ) ) )
                                                                                     .addQueryFilter( ValueFilter.create()
                                                                                                          .fieldName(
                                                                                                              BranchIndexPath.BRANCH_NAME.getPath() )
                                                                                                          .addValue(
                                                                                                              ValueFactory.newString(
                                                                                                                  internalContext.getBranch()
                                                                                                                      .getValue() ) )
                                                                                                          .build() )
                                                                                     .addOrderBy( FieldOrderExpr.create(
                                                                                         BranchIndexPath.PATH,
                                                                                         OrderExpr.Direction.DESC ) )
                                                                                     .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                                     .build(),
                                                                                 internalContext.getRepositoryId() ) );
    }

    public static Builder create()
    {
        return new DeleteNodeCommand.Builder();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private DeleteNodeListener deleteNodeListener;

        private RefreshMode refresh;

        private NodeId nodeId;

        private NodePath nodePath;

        private NodeStore nodeStore;

        private Builder()
        {
            super();
        }

        /** Phase 4 decision D2: optional — absent means the legacy {@code NodeBranchQuery} cascade. */
        public Builder nodeStore( final NodeStore nodeStore )
        {
            this.nodeStore = nodeStore;
            return this;
        }

        public Builder deleteNodeListener( final DeleteNodeListener deleteNodeListener )
        {
            this.deleteNodeListener = deleteNodeListener;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public DeleteNodeCommand build()
        {
            this.validate();
            return new DeleteNodeCommand( this );
        }
    }
}
