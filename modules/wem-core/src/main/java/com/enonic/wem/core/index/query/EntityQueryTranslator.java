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
    private QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory();

    private FilterBuilderFactory filterBuilderFactory = new FilterBuilderFactory();

    private SortBuilderFactory sortBuilderFactory = new SortBuilderFactory();

    private AggregationBuilderFactory aggregationBuilderFactory = new AggregationBuilderFactory();

    public ElasticsearchQuery translate( final EntityQuery entityQuery, final Workspace workspace )
    {
        ElasticsearchQuery elasticsearchQuery = ElasticsearchQuery.newQuery().
            index( workspace.getSearchIndexName() ).
            indexType( IndexType.NODE ).
            query( queryBuilderFactory.create( entityQuery.getQuery(), entityQuery.getQueryFilters() ) ).
            filter( filterBuilderFactory.create( entityQuery.getFilters() ) ).
            setAggregations( aggregationBuilderFactory.create( entityQuery.getAggregationQueries() ) ).
            sortBuilders( sortBuilderFactory.create( entityQuery.getOrderBys() ) ).
            from( entityQuery.getFrom() ).
            size( entityQuery.getSize() ).
            build();

        return elasticsearchQuery;
    }
}
