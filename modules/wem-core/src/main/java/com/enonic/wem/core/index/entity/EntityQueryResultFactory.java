package com.enonic.wem.core.index.entity;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;

import com.enonic.wem.core.index.aggregation.AggregationsFactory;

public class EntityQueryResultFactory
{
    private AggregationsFactory aggregationsFactory = new AggregationsFactory();

    public EntityQueryResult create( final SearchResponse searchResponse )
    {
        final SearchHits hits = searchResponse.getHits();

        return EntityQueryResult.newResult().
            hits( hits.getHits() != null ? hits.getHits().length : 0 ).
            totalHits( hits.totalHits() ).
            maxScore( hits.maxScore() ).
            addEntries( hits.getHits() ).
            aggregations( aggregationsFactory.create( searchResponse.getAggregations() ) ).
            build();
    }
}
