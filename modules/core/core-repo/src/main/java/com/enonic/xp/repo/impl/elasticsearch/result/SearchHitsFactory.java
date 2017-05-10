package com.enonic.xp.repo.impl.elasticsearch.result;

import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchHits;

public class SearchHitsFactory
{
    public static SearchHits create( final org.elasticsearch.search.SearchHits searchHits )
    {
        final SearchHits.Builder builder = SearchHits.create( searchHits.getTotalHits() ).
            maxScore( searchHits.maxScore() );

        for ( final org.elasticsearch.search.SearchHit hit : searchHits )
        {
            final SearchHit resultEntry = SearchHit.create().
                id( hit.id() ).
                score( hit.score() ).
                indexName( hit.getIndex() ).
                indexType( hit.getType() ).
                returnValues( ReturnValuesFactory.create( hit ) ).
                build();

            builder.add( resultEntry );
        }

        return builder.build();
    }
}
