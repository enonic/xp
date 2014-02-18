package com.enonic.wem.core.index.query;

import com.enonic.wem.api.entity.query.EntityQuery;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.elastic.ElasticsearchQuery;
import com.enonic.wem.core.index.query.aggregation.AggregationBuilderFactory;

public class EntityQueryTranslator
{
    private QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory();

    private FilterBuilderFactory filterBuilderFactory = new FilterBuilderFactory();

    private SortBuilderFactory sortBuilderFactory = new SortBuilderFactory();

    private AggregationBuilderFactory aggregationBuilderFactory = new AggregationBuilderFactory();

    public ElasticsearchQuery translate( final EntityQuery entityQuery )
    {
        ElasticsearchQuery elasticsearchQuery = ElasticsearchQuery.newQuery().
            index( Index.NODB ).
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
