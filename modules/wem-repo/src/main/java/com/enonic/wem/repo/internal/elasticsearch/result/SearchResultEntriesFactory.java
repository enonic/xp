package com.enonic.wem.repo.internal.elasticsearch.result;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.enonic.wem.repo.internal.index.result.SearchResultEntries;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;

class SearchResultEntriesFactory
{
    public static SearchResultEntries create( final SearchHits searchHits )
    {
        final SearchResultEntries.Builder builder = SearchResultEntries.create().
            totalHits( searchHits.getTotalHits() ).
            maxScore( searchHits.maxScore() );

        for ( final SearchHit hit : searchHits )
        {
            final SearchResultEntry resultEntry = SearchResultEntry.create().
                id( hit.id() ).
                score( hit.score() ).
                version( hit.version() ).
                setFields( SearchResultFieldsFactory.create( hit ) ).
                build();

            builder.add( resultEntry );
        }

        return builder.build();
    }

    public static SearchResultEntry create( final GetResponse getResponse )
    {
        return SearchResultEntry.create().
            id( getResponse.getId() ).
            setFields( SearchResultFieldsFactory.create( getResponse ) ).
            build();
    }
}
