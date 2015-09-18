package com.enonic.xp.repo.impl.elasticsearch.query.builder;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.DynamicConstraintExpr;
import com.enonic.xp.query.expr.Expression;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.NotExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;

public class QueryBuilderFactory
    extends AbstractQueryBuilderFactory
{
    private static QueryBuilder doCreate( final Builder builder )
    {
        final QueryBuilder queryBuilder;

        if ( builder.queryExpr != null )
        {
            final ConstraintExpr constraint = builder.queryExpr.getConstraint();

            queryBuilder = buildConstraint( constraint );
        }
        else
        {
            queryBuilder = QueryBuilders.matchAllQuery();
        }

        final Filters filters = builder.filterBuilder.build();

        final boolean wrapInFilteredQuery = filters != null && !filters.isEmpty();

        if ( wrapInFilteredQuery )
        {
            final FilterBuilder filterBuilder = FilterBuilderFactory.create( filters );
            return new FilteredQueryBuilder( queryBuilder, filterBuilder );
        }
        else
        {
            return queryBuilder;
        }
    }

    private static QueryBuilder buildConstraint( final Expression constraint )
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
            return DynamicQueryBuilderFactory.create( (DynamicConstraintExpr) constraint );
        }
        else if ( constraint instanceof CompareExpr )
        {
            return CompareQueryBuilderFactory.create( (CompareExpr) constraint );

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

    private static QueryBuilder buildNotExpr( final NotExpr expr )
    {
        final QueryBuilder negated = buildConstraint( expr.getExpression() );
        return buildNotQuery( negated );
    }


    private static QueryBuilder buildLogicalExpression( final LogicalExpr expr )
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


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private QueryExpr queryExpr;

        private final Filters.Builder filterBuilder = Filters.create();

        public Builder queryExpr( final QueryExpr queryExpr )
        {
            this.queryExpr = queryExpr;
            return this;
        }

        public Builder addQueryFilter( final Filter filter )
        {
            if ( filter != null )
            {
                filterBuilder.add( filter );
            }

            return this;
        }

        public Builder addQueryFilters( final Filters filters )
        {
            filterBuilder.addAll( filters.getSet() );
            return this;
        }

        public QueryBuilder build()
        {
            return QueryBuilderFactory.doCreate( this );
        }

    }

}
