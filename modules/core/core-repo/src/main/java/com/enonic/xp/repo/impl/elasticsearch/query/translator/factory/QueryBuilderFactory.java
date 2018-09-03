package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.google.common.base.Preconditions;

import com.enonic.xp.query.expr.ConstraintExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.query.ConstraintExpressionBuilder;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;

public class QueryBuilderFactory
    extends AbstractBuilderFactory
{
    private final QueryExpr queryExpr;

    private final Filters filters;

    private QueryBuilderFactory( final Builder builder )
    {
        super( builder.fieldNameResolver );
        this.queryExpr = builder.queryExpr;
        this.filters = builder.filterBuilder.build();
    }

    public QueryBuilder create()
    {
        final QueryBuilder queryBuilder;

        if ( queryExpr != null )
        {
            final ConstraintExpr constraint = queryExpr.getConstraint();

            queryBuilder = ConstraintExpressionBuilder.build( constraint, fieldNameResolver );
        }
        else
        {
            queryBuilder = QueryBuilders.matchAllQuery();
        }

        final boolean wrapInFilteredQuery = filters != null && !filters.isEmpty();

        if ( wrapInFilteredQuery )
        {
            final QueryBuilder filterBuilder = new FilterBuilderFactory( fieldNameResolver ).create( filters );
            return new FilteredQueryBuilder( queryBuilder, filterBuilder );
        }
        else
        {
            return queryBuilder;
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private QueryExpr queryExpr;

        private final Filters.Builder filterBuilder = Filters.create();

        private QueryFieldNameResolver fieldNameResolver;

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
            filterBuilder.addAll( filters.getList() );
            return this;
        }

        public Builder fieldNameResolver( final QueryFieldNameResolver fieldNameResolver )
        {
            this.fieldNameResolver = fieldNameResolver;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( fieldNameResolver );
        }

        public QueryBuilderFactory build()
        {
            validate();
            return new QueryBuilderFactory( this );
        }

    }

}
