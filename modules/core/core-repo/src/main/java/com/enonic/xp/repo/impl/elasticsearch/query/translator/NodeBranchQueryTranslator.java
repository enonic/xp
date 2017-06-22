package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.StoreQueryFieldNameResolver;

class NodeBranchQueryTranslator
    implements QueryTypeTranslator
{
    private final QueryFieldNameResolver fieldNameResolver = new StoreQueryFieldNameResolver();

    private final NodeBranchQuery query;

    NodeBranchQueryTranslator( final NodeBranchQuery query )
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
