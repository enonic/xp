package com.enonic.wem.core.elasticsearch.result;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;

import com.enonic.wem.core.elasticsearch.aggregation.AggregationsFactory;
import com.enonic.wem.core.index.result.SearchResult;

public class SearchResultFactory
{
    public static SearchResult create( final SearchResponse searchResponse )
    {
        return SearchResult.create().
            results( SearchResultEntriesFactory.create( searchResponse.getHits() ) ).
            aggregations( AggregationsFactory.create( searchResponse.getAggregations() ) ).
            build();
    }

    public static SearchResult create( final GetResponse getResponse )
    {
        return SearchResult.create().
            results( SearchResultEntriesFactory.create( getResponse ) ).
            build();
    }
}
