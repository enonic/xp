package com.enonic.xp.repo.impl.node;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static java.util.Objects.requireNonNull;

/**
 * Finds all branch entries below a parent path by querying the branch storage index,
 * so that storage operations never depend on the search index.
 */
final class FindNodeBranchEntriesByParentCommand
    extends AbstractNodeCommand
{
    private final NodePath parentPath;

    private final boolean recursive;

    private final OrderExpr.Direction pathOrder;

    private final Permission requiredPermission;

    private final boolean refreshStorage;

    private final int batchSize;

    private final String cursor;

    private FindNodeBranchEntriesByParentCommand( final Builder builder )
    {
        super( builder );
        this.parentPath = builder.parentPath;
        this.recursive = builder.recursive;
        this.pathOrder = builder.pathOrder;
        this.requiredPermission = builder.requiredPermission;
        this.refreshStorage = builder.refreshStorage;
        this.batchSize = builder.batchSize;
        this.cursor = builder.cursor;
    }

    static Builder create()
    {
        return new Builder();
    }

    static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    NodeBranchEntries execute()
    {
        return executeBatch().entries();
    }

    /**
     * One batch of the listing and the position it stopped at, or the whole listing and no position where no batch size is set. The
     * cursor is the id of the last entry scanned rather than the last entry kept, so a continuation never revisits ground that
     * {@link #filter} discarded; a batched scan orders by id rather than by path, so a move between batches cannot carry a node across
     * the cursor.
     */
    Batch executeBatch()
    {
        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        if ( refreshStorage )
        {
            refresh( RefreshMode.STORAGE );
        }

        final NodeBranchQuery.Builder query = createSubtreeQuery( context )
            .addOrderBy( FieldOrderExpr.create( batchSize > 0 ? BranchIndexPath.NODE_ID : BranchIndexPath.PATH,
                                                batchSize > 0 ? OrderExpr.Direction.ASC : pathOrder ) )
            .size( batchSize > 0 ? batchSize : NodeSearchService.GET_ALL_SIZE_FLAG );

        if ( cursor != null )
        {
            query.addQueryFilter(
                RangeFilter.create().fieldName( BranchIndexPath.NODE_ID.getPath() ).gt( ValueFactory.newString( cursor ) ).build() );
        }

        final NodeBranchEntries entries =
            NodeBranchQueryResultFactory.create( this.nodeSearchService.query( query.build(), context.getRepositoryId() ) );

        final String nextCursor =
            batchSize > 0 && entries.getSize() == batchSize ? Iterables.getLast( entries ).getNodeId().toString() : null;

        return new Batch( filter( entries, context ), nextCursor );
    }

    record Batch(NodeBranchEntries entries, String cursor)
    {
    }

    /**
     * The subtree as bare id-and-path pairs, ordered by path, for a walker that reads nothing else of an entry - fetching neither
     * version ids, blob keys nor timestamps. A permission requirement is not supported here, since deciding one costs exactly the
     * access control key this projection does not fetch.
     */
    List<IdAndPath> executeIdAndPaths()
    {
        if ( requiredPermission != null )
        {
            throw new IllegalStateException( "a permission filter expects full entries" );
        }

        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        if ( refreshStorage )
        {
            refresh( RefreshMode.STORAGE );
        }

        final NodeBranchQuery query = createSubtreeQuery( context )
            .addOrderBy( FieldOrderExpr.create( BranchIndexPath.PATH, pathOrder ) )
            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
            .returnFields( ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.PATH ) )
            .build();

        return this.nodeSearchService.query( query, context.getRepositoryId() )
            .getHits()
            .stream()
            .map( SearchHit::getReturnValues )
            .map( values -> new IdAndPath( NodeId.from( values.getStringValue( BranchIndexPath.NODE_ID ) ),
                                           new NodePath( values.getStringValue( BranchIndexPath.PATH ) ) ) )
            .filter( entry -> recursive || parentPath.equals( entry.nodePath().getParentPath() ) )
            .collect( ImmutableList.toImmutableList() );
    }

    record IdAndPath(NodeId nodeId, NodePath nodePath)
    {
    }

    private NodeBranchQuery.Builder createSubtreeQuery( final InternalContext context )
    {
        return NodeBranchQuery.create()
            .query( QueryExpr.from( createBelowParentExpr() ) )
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( context.getBranch().getValue() ) )
                                 .build() );
    }

    /**
     * The branch index carries no parentPath field, so a parent is matched by a path prefix, which the index answers by seeking its term
     * dictionary once and scanning the subtree from there. Narrowing that to the direct children would take a wildcard with an inner
     * {@code *}, which no longer reduces to a prefix and makes the index run an automaton over every term of the subtree instead, so the
     * deeper levels are dropped in {@link #filter} rather than here.
     */
    private ConstraintExpr createBelowParentExpr()
    {
        return parentPath.isRoot()
            ? CompareExpr.neq( FieldExpr.from( BranchIndexPath.PATH ), ValueExpr.string( NodePath.ROOT.toString() ) )
            : CompareExpr.like( FieldExpr.from( BranchIndexPath.PATH ), ValueExpr.string( parentPath + "/*" ) );
    }

    /**
     * Drops the descendants a non-recursive listing did not ask for, and the entries the caller may not read. Depth is settled first,
     * since it costs a path comparison whereas a permission decision costs a stored read of the access control list of the entry.
     */
    private NodeBranchEntries filter( final NodeBranchEntries entries, final InternalContext context )
    {
        final boolean checkPermission = requiredPermission != null && !context.getPrincipalKeys().contains( RoleKeys.ADMIN );

        if ( recursive && !checkPermission )
        {
            return entries;
        }

        final NodeBranchEntries.Builder filtered = NodeBranchEntries.create();
        for ( final NodeBranchEntry entry : entries )
        {
            if ( !recursive && !parentPath.equals( entry.getNodePath().getParentPath() ) )
            {
                continue;
            }

            if ( checkPermission )
            {
                final AccessControlList permissions = this.nodeStorageService.getNodePermissions( entry.getNodeVersionKey(), context );
                if ( !NodePermissionsResolver.hasPermission( context.getPrincipalKeys(), requiredPermission, permissions ) )
                {
                    continue;
                }
            }

            filtered.add( entry );
        }
        return filtered.build();
    }

    static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath parentPath;

        private boolean recursive = true;

        private OrderExpr.Direction pathOrder = OrderExpr.Direction.ASC;

        private Permission requiredPermission;

        private boolean refreshStorage = true;

        private int batchSize;

        private String cursor;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        Builder parentPath( final NodePath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        Builder pathOrder( final OrderExpr.Direction pathOrder )
        {
            this.pathOrder = pathOrder;
            return this;
        }

        Builder requiredPermission( final Permission requiredPermission )
        {
            this.requiredPermission = requiredPermission;
            return this;
        }

        Builder refreshStorage( final boolean refreshStorage )
        {
            this.refreshStorage = refreshStorage;
            return this;
        }

        Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        Builder cursor( final String cursor )
        {
            this.cursor = cursor;
            return this;
        }

        FindNodeBranchEntriesByParentCommand build()
        {
            validate();
            return new FindNodeBranchEntriesByParentCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            requireNonNull( parentPath, "parentPath is required" );
        }
    }
}
