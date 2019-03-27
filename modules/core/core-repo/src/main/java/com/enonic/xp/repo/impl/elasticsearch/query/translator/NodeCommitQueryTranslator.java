package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.node.NodeCommitQuery;
import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.StoreQueryFieldNameResolver;

class NodeCommitQueryTranslator
    implements QueryTypeTranslator
{
    private final QueryFieldNameResolver fieldNameResolver = new StoreQueryFieldNameResolver();

    private final NodeCommitQuery query;

    NodeCommitQueryTranslator( final NodeCommitQuery query )
    {
        this.query = query;
    }

    @Override
    public QueryFieldNameResolver getFieldNameResolver()
    {
        return this.fieldNameResolver;
    }

    @Override
    public int getBatchSize()
    {
        return this.query.getBatchSize();
    }

    @Override
    public SearchOptimizer getSearchOptimizer()
    {
        return this.query.getSearchOptimizer();
    }

    @Override
    public QueryBuilder createQueryBuilder( final Filters additionalFilters )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.newBuilder().
            queryExpr( this.query.getQuery() ).
            addQueryFilters( this.query.getQueryFilters() ).
            addQueryFilters( additionalFilters ).
            fieldNameResolver( this.fieldNameResolver );

        return queryBuilderBuilder.build().create();
    }
}
