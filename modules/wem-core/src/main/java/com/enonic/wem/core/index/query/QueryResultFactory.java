package com.enonic.wem.core.index.query;

import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntries;
import com.enonic.wem.core.elasticsearch.result.SearchResultEntry;

public class QueryResultFactory
{
    public QueryResult create( final SearchResult searchResult )
    {
        final SearchResultEntries results = searchResult.getResults();

        final QueryResult.Builder builder = QueryResult.newQueryResult().
            hits( results.getSize() ).
            totalHits( results.getTotalHits() ).
            maxScore( results.getMaxScore() );

        for ( final SearchResultEntry result : results )
        {
            builder.addEntry( new QueryResultEntry( result.getScore(), result.getId() ) );
        }

        builder.aggregations( searchResult.getAggregations() );

        return builder.build();
    }

}
