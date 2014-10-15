package com.enonic.wem.core.elasticsearch.query;

import com.enonic.wem.core.elasticsearch.aggregation.AggregationBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.FilterBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.core.entity.query.EntityQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.repository.IndexNameResolver;

class EntityQueryTranslator
{
    public static ElasticsearchQuery translate( final EntityQuery entityQuery, final IndexContext indexContext )
    {
        ElasticsearchQuery elasticsearchQuery = ElasticsearchQuery.newQuery().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( indexContext.getWorkspace().getName() ).
            query( QueryBuilderFactory.create().
                queryExpr( entityQuery.getQuery() ).
                addQueryFilters( entityQuery.getQueryFilters() ).
                build() ).
            filter( FilterBuilderFactory.create( entityQuery.getPostFilters() ) ).
            setAggregations( AggregationBuilderFactory.create( entityQuery.getAggregationQueries() ) ).
            sortBuilders( SortQueryBuilderFactory.create( entityQuery.getOrderBys() ) ).
            from( entityQuery.getFrom() ).
            size( entityQuery.getSize() ).
            build();

        return elasticsearchQuery;
    }
}
