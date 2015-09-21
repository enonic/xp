package com.enonic.wem.repo.internal.index.query;

import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchHits;
import com.enonic.wem.repo.internal.storage.result.SearchResult;

public class QueryResultFactory
{
    public static NodeQueryResult create( final SearchResult searchResult )
    {
        final SearchHits results = searchResult.getResults();

        final NodeQueryResult.Builder builder = NodeQueryResult.create().
            hits( results.getSize() ).
            totalHits( results.getTotalHits() ).
            maxScore( results.getMaxScore() );

        for ( final SearchHit result : results )
        {
            builder.addEntry( new NodeQueryResultEntry( result.getScore(), result.getId() ) );
        }

        builder.aggregations( searchResult.getAggregations() );

        return builder.build();
    }

}
