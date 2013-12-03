package com.enonic.wem.core.index.entity;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;

public class EntitySearchResultFactory
{
    public EntitySearchResult create( final SearchResponse searchResponse )
    {
        final SearchHits hits = searchResponse.getHits();

        return EntitySearchResult.newResult().
            hits( hits.getHits().length ).
            totalHits( hits.totalHits() ).
            addEntries( hits.getHits() ).
            build();
    }

}
