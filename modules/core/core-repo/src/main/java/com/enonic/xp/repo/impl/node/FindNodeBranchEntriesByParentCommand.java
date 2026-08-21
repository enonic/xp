package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.google.common.collect.Iterables;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static java.util.Objects.requireNonNull;

final class FindNodeBranchEntriesByParentCommand
    extends AbstractNodeCommand
{
    private final NodePath parentPath;

    private final OrderExpr.Direction pathOrder;

    private final Permission requiredPermission;

    private final boolean refreshStorage;

    private final int batchSize;

    private final Instant modifiedBefore;

    private final String cursor;

    private FindNodeBranchEntriesByParentCommand( final Builder builder )
    {
        super( builder );
        this.parentPath = builder.parentPath;
        this.pathOrder = builder.pathOrder;
        this.requiredPermission = builder.requiredPermission;
        this.refreshStorage = builder.refreshStorage;
        this.batchSize = builder.batchSize;
        this.modifiedBefore = builder.modifiedBefore;
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

    Batch executeBatch()
    {
        final InternalContext context = InternalContext.from( ContextAccessor.current() );

        if ( refreshStorage )
        {
            refresh( RefreshMode.STORAGE );
        }

        final NodeBranchQuery.Builder query = NodeBranchQuery.create()
            .query( QueryExpr.from( createBelowParentExpr() ) )
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( context.getBranch().getValue() ) )
                                 .build() )
            .size( batchSize > 0 ? batchSize : NodeSearchService.GET_ALL_SIZE_FLAG );

        if ( modifiedBefore != null )
        {
            query.addQueryFilter( RangeFilter.create()
                                      .fieldName( BranchIndexPath.TIMESTAMP.getPath() )
                                      .lt( ValueFactory.newDateTime( modifiedBefore ) )
                                      .build() )
                .addOrderBy( FieldOrderExpr.create( BranchIndexPath.TIMESTAMP, OrderExpr.Direction.ASC ) )
                .addOrderBy( FieldOrderExpr.create( BranchIndexPath.NODE_ID, OrderExpr.Direction.ASC ) );
        }
        else
        {
            query.addOrderBy( FieldOrderExpr.create( batchSize > 0 ? BranchIndexPath.NODE_ID : BranchIndexPath.PATH,
                                                     batchSize > 0 ? OrderExpr.Direction.ASC : pathOrder ) );
        }

        if ( cursor != null )
        {
            query.addQueryFilter( createAfterCursorFilter() );
        }

        final SearchResult result = this.nodeSearchService.query( query.build(), context.getRepositoryId() );

        final NodeBranchEntries entries = NodeBranchQueryResultFactory.create( result );

        final String nextCursor = batchSize > 0 && entries.getSize() == batchSize ? cursorOf( Iterables.getLast( entries ) ) : null;

        return new Batch( filter( entries, context ), nextCursor, result.getTotalHits() );
    }

    private String cursorOf( final NodeBranchEntry entry )
    {
        return modifiedBefore == null
            ? entry.getNodeId().toString()
            : entry.getTimestamp().toEpochMilli() + "|" + entry.getNodeId();
    }

    private Filter createAfterCursorFilter()
    {
        if ( modifiedBefore == null )
        {
            return RangeFilter.create().fieldName( BranchIndexPath.NODE_ID.getPath() ).gt( ValueFactory.newString( cursor ) ).build();
        }

        final int separator = cursor.indexOf( '|' );
        final Value cursorTimestamp;
        try
        {
            cursorTimestamp = ValueFactory.newDateTime( Instant.ofEpochMilli( Long.parseLong( cursor.substring( 0, separator ) ) ) );
        }
        catch ( IndexOutOfBoundsException | NumberFormatException e )
        {
            throw new IllegalArgumentException( "Invalid cursor: " + cursor, e );
        }
        final String cursorId = cursor.substring( separator + 1 );

        return BooleanFilter.create()
            .should( RangeFilter.create().fieldName( BranchIndexPath.TIMESTAMP.getPath() ).gt( cursorTimestamp ).build() )
            .should( BooleanFilter.create()
                         .must( RangeFilter.create()
                                    .fieldName( BranchIndexPath.TIMESTAMP.getPath() )
                                    .from( cursorTimestamp )
                                    .to( cursorTimestamp )
                                    .build() )
                         .must( RangeFilter.create()
                                    .fieldName( BranchIndexPath.NODE_ID.getPath() )
                                    .gt( ValueFactory.newString( cursorId ) )
                                    .build() )
                         .build() )
            .build();
    }

    record Batch(NodeBranchEntries entries, String cursor, long totalHits)
    {
    }

    private ConstraintExpr createBelowParentExpr()
    {
        return parentPath.isRoot()
            ? CompareExpr.neq( FieldExpr.from( BranchIndexPath.PATH ), ValueExpr.string( NodePath.ROOT.toString() ) )
            : CompareExpr.like( FieldExpr.from( BranchIndexPath.PATH ), ValueExpr.string( parentPath + "/*" ) );
    }

    private NodeBranchEntries filter( final NodeBranchEntries entries, final InternalContext context )
    {
        if ( requiredPermission == null || context.getPrincipalKeys().contains( RoleKeys.ADMIN ) )
        {
            return entries;
        }

        final NodeBranchEntries.Builder filtered = NodeBranchEntries.create();
        for ( final NodeBranchEntry entry : entries )
        {
            final AccessControlList permissions = this.nodeStorageService.getNodePermissions( entry.getNodeVersionKey(), context );
            if ( NodePermissionsResolver.hasPermission( context.getPrincipalKeys(), requiredPermission, permissions ) )
            {
                filtered.add( entry );
            }
        }
        return filtered.build();
    }

    static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodePath parentPath;

        private OrderExpr.Direction pathOrder = OrderExpr.Direction.ASC;

        private Permission requiredPermission;

        private boolean refreshStorage = true;

        private int batchSize;

        private Instant modifiedBefore;

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

        Builder modifiedBefore( final Instant modifiedBefore )
        {
            this.modifiedBefore = modifiedBefore;
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
