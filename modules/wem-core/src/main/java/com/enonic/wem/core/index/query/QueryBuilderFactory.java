package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.query.expr.CompareExpr;
import com.enonic.wem.query.expr.ConstraintExpr;
import com.enonic.wem.query.expr.DynamicConstraintExpr;
import com.enonic.wem.query.expr.Expression;
import com.enonic.wem.query.expr.LogicalExpr;
import com.enonic.wem.query.expr.NotExpr;
import com.enonic.wem.query.expr.QueryExpr;
import com.enonic.wem.query.queryfilter.QueryFilter;

public class QueryBuilderFactory
    extends AbstractBuilderFactory
{
    private CompareQueryFactory compareQueryFactory = new CompareQueryFactory();

    private DynamicQueryFactory dynamicQueryFactory = new DynamicQueryFactory();

    private FilterBuilderFactory filterBuilderFactory = new FilterBuilderFactory();

    public QueryBuilder create( final QueryExpr queryExpr, final ImmutableSet<QueryFilter> queryFilters )
    {
        final QueryBuilder queryBuilder;

        if ( queryExpr != null )
        {
            final ConstraintExpr constraint = queryExpr.getConstraint();

            queryBuilder = buildConstraint( constraint );
        }
        else
        {
            queryBuilder = QueryBuilders.matchAllQuery();
        }

        final boolean wrapInFilteredQuery = queryFilters != null && !queryFilters.isEmpty();

        if ( wrapInFilteredQuery )
        {
            final FilterBuilder filterBuilder = filterBuilderFactory.create( queryFilters );
            return new FilteredQueryBuilder( queryBuilder, filterBuilder );
        }
        else
        {
            return queryBuilder;
        }

    }

    private QueryBuilder buildConstraint( final Expression constraint )
    {
        if ( constraint == null )
        {
            return QueryBuilders.matchAllQuery();
        }
        else if ( constraint instanceof LogicalExpr )
        {
            return buildLogicalExpression( (LogicalExpr) constraint );
        }
        else if ( constraint instanceof DynamicConstraintExpr )
        {
            return dynamicQueryFactory.create( (DynamicConstraintExpr) constraint );

        }
        else if ( constraint instanceof CompareExpr )
        {
            return compareQueryFactory.create( (CompareExpr) constraint );

        }
        else if ( constraint instanceof NotExpr )
        {
            return buildNotExpr( (NotExpr) constraint );
        }
        else
        {
            throw new UnsupportedOperationException( "Not able to handle expression of type " + constraint.getClass() );
        }
    }

    private QueryBuilder buildNotExpr( final NotExpr expr )
    {
        final QueryBuilder negated = buildConstraint( expr.getExpression() );
        return buildNotQuery( negated );
    }


    private QueryBuilder buildLogicalExpression( final LogicalExpr expr )
    {

        final QueryBuilder left = buildConstraint( expr.getLeft() );
        final QueryBuilder right = buildConstraint( expr.getRight() );

        if ( expr.getOperator() == LogicalExpr.Operator.OR )
        {
            return QueryBuilders.boolQuery().should( left ).should( right );
        }
        else if ( expr.getOperator() == LogicalExpr.Operator.AND )
        {
            return QueryBuilders.boolQuery().must( left ).must( right );
        }
        else
        {
            throw new IllegalArgumentException( "Operation [" + expr.getOperator() + "] not supported" );
        }
    }

}
