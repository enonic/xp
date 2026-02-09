package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.DiffQueryFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.QueryFieldNameResolver;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.resolver.StoreQueryFieldNameResolver;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

class NodeVersionDiffQueryTranslator
    implements QueryTypeTranslator
{
    private final NodeVersionDiffQuery query;

    private final QueryFieldNameResolver fieldNameResolver = StoreQueryFieldNameResolver.INSTANCE;

    NodeVersionDiffQueryTranslator( final NodeVersionDiffQuery query )
    {
        this.query = query;
    }

    @Override
    public QueryBuilder createQueryBuilder( final Filters additionalFilters )
    {
        return DiffQueryFactory.create().query( query ).build().execute();
    }

    @Override
    public QueryFieldNameResolver getFieldNameResolver()
    {
        return this.fieldNameResolver;
    }

    @Override
    public int getBatchSize()
    {
        return query.getBatchSize();
    }

    @Override
    public SearchOptimizer getSearchOptimizer()
    {
        return query.getSearchOptimizer();
    }
}
