package com.enonic.xp.repo.impl.index.query;

import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class NodeQueryResultFactory
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
