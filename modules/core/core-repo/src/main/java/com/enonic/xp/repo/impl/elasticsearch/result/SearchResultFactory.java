package com.enonic.xp.repo.impl.elasticsearch.result;

import org.elasticsearch.action.search.SearchResponse;

import com.enonic.xp.repo.impl.elasticsearch.aggregation.AggregationsFactory;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class SearchResultFactory
{
    public static SearchResult create( final SearchResponse searchResponse )
    {
        return SearchResult.create().
            hits( SearchHitsFactory.create( searchResponse.getHits() ) ).
            totalHits( searchResponse.getHits().getTotalHits() ).
            maxScore( searchResponse.getHits().maxScore() ).
            aggregations( AggregationsFactory.create( searchResponse.getAggregations() ) ).
            build();
    }
}
