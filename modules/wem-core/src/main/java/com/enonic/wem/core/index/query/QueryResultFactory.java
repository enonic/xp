package com.enonic.wem.core.index.query;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.enonic.wem.core.index.aggregation.AggregationsFactory;

public class QueryResultFactory
{
    private AggregationsFactory aggregationsFactory = new AggregationsFactory();

    public QueryResult create( final SearchResponse searchResponse )
    {
        final SearchHits hits = searchResponse.getHits();

        final QueryResult.Builder builder = QueryResult.newQueryResult().
            hits( hits.getHits() != null ? hits.getHits().length : 0 ).
            totalHits( hits.totalHits() ).
            maxScore( hits.maxScore() );

        for ( final SearchHit hit : hits )
        {
            builder.addEntry( new QueryResultEntry( hit.score(), hit.id() ) );
        }

        builder.aggregations( aggregationsFactory.create( searchResponse.getAggregations() ) );

        return builder.build();
    }

}
