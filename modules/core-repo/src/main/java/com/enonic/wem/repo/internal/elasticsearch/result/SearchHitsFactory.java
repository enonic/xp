package com.enonic.wem.repo.internal.elasticsearch.result;

import com.enonic.wem.repo.internal.index.result.SearchHit;
import com.enonic.wem.repo.internal.index.result.SearchHits;

class SearchHitsFactory
{
    public static SearchHits create( final org.elasticsearch.search.SearchHits searchHits )
    {
        final SearchHits.Builder builder = SearchHits.create().
            totalHits( searchHits.getTotalHits() ).
            maxScore( searchHits.maxScore() );

        for ( final org.elasticsearch.search.SearchHit hit : searchHits )
        {
            final SearchHit resultEntry = SearchHit.create().
                id( hit.id() ).
                score( hit.score() ).
                version( hit.version() ).
                returnValues( ReturnValuesFactory.create( hit ) ).
                build();

            builder.add( resultEntry );
        }

        return builder.build();
    }
}
