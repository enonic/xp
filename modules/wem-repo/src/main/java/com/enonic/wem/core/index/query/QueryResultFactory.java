package com.enonic.wem.core.index.query;

import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntries;
import com.enonic.wem.core.index.result.SearchResultEntry;

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
