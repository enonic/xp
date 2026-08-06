package com.enonic.xp.repo.impl.search.dsl;

import java.util.Map;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.AbstractQuery;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.Query;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.storage.spi.SearchDsl;

/**
 * Renders a {@link Query} into the canonical JSON query DSL plus the paging parameters that
 * ride with it — the wire format of Phase 4's search path.
 * <p>
 * This lives above the storage SPI, beside the NoQL parser's output rather than inside a
 * backend, so that exactly one component in the system knows the query language. A backend
 * receiving a {@link SearchDsl} is a serializer.
 * <p>
 * Aggregations, suggesters and highlighting are deliberately NOT rendered here: they are
 * owned by later gates and fail loudly rather than being shipped half-translated.
 */
public final class SearchDslRenderer
{
    private SearchDslRenderer()
    {
    }

    public static SearchDsl render( final Query query )
    {
        if ( query == null )
        {
            throw new DslRenderException( "Cannot render a null query" );
        }

        final SearchDsl.Builder builder = SearchDsl.create();

        final QueryExpr queryExpr = query.getQuery();
        builder.query( ConstraintDslRenderer.render( queryExpr == null ? null : queryExpr.getConstraint() ) );

        for ( final Filter filter : query.getQueryFilters() )
        {
            builder.addQueryFilter( FilterDslRenderer.render( filter ) );
        }

        final Filter parentFilter = parentFilter( query );
        if ( parentFilter != null )
        {
            builder.addQueryFilter( FilterDslRenderer.render( parentFilter ) );
        }

        for ( final Filter filter : query.getPostFilters() )
        {
            builder.addPostFilter( FilterDslRenderer.render( filter ) );
        }

        for ( final OrderExpr orderExpr : query.getOrderBys() )
        {
            builder.addSort( OrderDslRenderer.render( orderExpr ) );
        }

        rejectUnrenderable( query );

        return builder.from( query.getFrom() )
            .size( query.getSize() )
            .batchSize( query instanceof AbstractQuery ? ( (AbstractQuery) query ).getBatchSize() : 0 )
            .explain( query.isExplain() )
            .searchOptimizer( query instanceof AbstractQuery
                                  ? ( (AbstractQuery) query ).getSearchOptimizer().name()
                                  : com.enonic.xp.node.SearchOptimizer.SPEED.name() )
            .build();
    }

    /** Renders one constraint tree on its own — the unit the round-trip oracle exercises. */
    public static Map<String, Object> renderConstraint( final QueryExpr queryExpr )
    {
        return ConstraintDslRenderer.render( queryExpr == null ? null : queryExpr.getConstraint() );
    }

    public static Map<String, Object> renderOrder( final OrderExpr orderExpr )
    {
        return OrderDslRenderer.render( orderExpr );
    }

    /**
     * Mirrors {@code NodeQueryTranslator}: a parent constraint is an additional query filter,
     * not part of the query expression.
     */
    private static Filter parentFilter( final Query query )
    {
        if ( !( query instanceof NodeQuery ) || ( (NodeQuery) query ).getParent() == null )
        {
            return null;
        }
        return ValueFilter.create()
            .fieldName( NodeIndexPath.PARENT_PATH.getPath() )
            .addValue( ValueFactory.newString( ( (NodeQuery) query ).getParent().toString() ) )
            .build();
    }

    private static void rejectUnrenderable( final Query query )
    {
        if ( !query.getAggregationQueries().isEmpty() )
        {
            throw new DslRenderException( "Aggregations have no wire form yet; they are not supported by this storage backend" );
        }
        if ( !query.getSuggestionQueries().isEmpty() )
        {
            throw new DslRenderException( "Suggesters have no wire form yet; they are not supported by this storage backend" );
        }
        if ( query.getHighlight() != null )
        {
            throw new DslRenderException( "Highlighting has no wire form yet; it is not supported by this storage backend" );
        }
    }
}
