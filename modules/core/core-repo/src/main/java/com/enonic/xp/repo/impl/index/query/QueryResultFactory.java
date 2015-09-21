package com.enonic.xp.repo.impl.index.query;

import com.enonic.xp.repo.impl.index.result.SearchResult;
import com.enonic.xp.repo.impl.index.result.SearchResultEntries;
import com.enonic.xp.repo.impl.index.result.SearchResultEntry;

public class QueryResultFactory
{
    public NodeQueryResult create( final SearchResult searchResult )
    {
        final SearchResultEntries results = searchResult.getResults();

        final NodeQueryResult.Builder builder = NodeQueryResult.create().
            hits( results.getSize() ).
            totalHits( results.getTotalHits() ).
            maxScore( results.getMaxScore() );

        for ( final SearchResultEntry result : results )
        {
            builder.addEntry( new NodeQueryResultEntry( result.getScore(), result.getId() ) );
        }

        builder.aggregations( searchResult.getAggregations() );

        return builder.build();
    }

}
