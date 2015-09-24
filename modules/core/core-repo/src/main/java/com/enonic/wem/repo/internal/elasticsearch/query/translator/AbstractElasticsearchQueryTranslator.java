package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.repo.internal.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.xp.query.Query;

abstract class AbstractElasticsearchQueryTranslator
{
    static QueryBuilderFactory.Builder createQuery( final Query nodeQuery )
    {
        return QueryBuilderFactory.create().
            queryExpr( nodeQuery.getQuery() ).
            addQueryFilters( nodeQuery.getQueryFilters() );
    }

    static ElasticsearchQuery doCreateEsQuery( final Query query, final SearchRequest searchRequest, final QueryBuilder queryWithFilters )
    {
        final StorageSettings settings = searchRequest.getSettings();

        final ElasticsearchQuery.Builder queryBuilder = ElasticsearchQuery.create().
            index( settings.getStorageName().getName() ).
            indexType( settings.getStorageType().getName() ).
            query( queryWithFilters ).
            setAggregations( AggregationQueryBuilderFactory.create( query.getAggregationQueries() ) ).
            sortBuilders( SortQueryBuilderFactory.create( query.getOrderBys() ) ).
            filter( FilterBuilderFactory.create( query.getPostFilters() ) ).
            setReturnFields( searchRequest.getReturnFields() ).
            from( query.getFrom() ).
            size( query.getSize() );

        return queryBuilder.build();
    }
}
