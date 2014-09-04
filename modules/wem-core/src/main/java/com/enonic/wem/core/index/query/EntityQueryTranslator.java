package com.enonic.wem.core.index.query;

import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.elasticsearch.ElasticsearchQuery;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.query.aggregation.AggregationBuilderFactory;
import com.enonic.wem.core.index.query.builder.FilterBuilderFactory;
import com.enonic.wem.core.index.query.builder.QueryBuilderFactory;
import com.enonic.wem.core.index.query.builder.SortBuilderFactory;

public class EntityQueryTranslator
{
    public static ElasticsearchQuery translate( final EntityQuery entityQuery, final Workspace workspace )
    {
        ElasticsearchQuery elasticsearchQuery = ElasticsearchQuery.newQuery().
            index( workspace.getSearchIndexName() ).
            indexType( IndexType.NODE ).
            query( QueryBuilderFactory.create().
                queryExpr( entityQuery.getQuery() ).
                addQueryFilters( entityQuery.getQueryFilters() ).
                build() ).
            filter( FilterBuilderFactory.create( entityQuery.getPostFilters() ) ).
            setAggregations( AggregationBuilderFactory.create( entityQuery.getAggregationQueries() ) ).
            sortBuilders( SortBuilderFactory.create( entityQuery.getOrderBys() ) ).
            from( entityQuery.getFrom() ).
            size( entityQuery.getSize() ).
            build();

        return elasticsearchQuery;
    }
}
