package com.enonic.xp.repo.impl.elasticsearch.result;

import org.elasticsearch.action.search.SearchResponse;

import com.enonic.xp.repo.impl.elasticsearch.aggregation.AggregationsFactory;
import com.enonic.xp.repo.impl.index.result.SearchResult;

public class SearchResultFactory
{
    public static SearchResult create( final SearchResponse searchResponse )
    {
        return SearchResult.create().
            results( SearchResultEntriesFactory.create( searchResponse.getHits() ) ).
            aggregations( AggregationsFactory.create( searchResponse.getAggregations() ) ).
            build();
    }


}
