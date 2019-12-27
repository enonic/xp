package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class CountExecutor
    extends AbstractExecutor
{

    private CountExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final EsClient client )
    {
        return new Builder( client );
    }

    public SearchResult execute( final ElasticsearchQuery query )
    {
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().
            query( query.getQuery() ).
            postFilter( query.getFilter() );

        query.getAggregations().forEach( sourceBuilder::aggregation );

        final CountRequest countRequest = new CountRequest().
            indices( query.getIndexNames() ).
            preference( searchPreference ).
            source( sourceBuilder );

        final CountResponse countResponse = client.count( countRequest );

        return SearchResult.create().
            hits( SearchHits.create().build() ).
            totalHits( countResponse.getCount() ).build();
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final EsClient client )
        {
            super( client );
        }


        public CountExecutor build()
        {
            return new CountExecutor( this );
        }
    }
}
