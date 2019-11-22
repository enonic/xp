package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

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

    public static Builder create( final RestHighLevelClient client )
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

        try
        {
            final CountResponse countResponse = client.count( countRequest, RequestOptions.DEFAULT );

            return SearchResult.create().
                hits( SearchHits.create().build() ).
                totalHits( countResponse.getCount() ).build();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final RestHighLevelClient client )
        {
            super( client );
        }


        public CountExecutor build()
        {
            return new CountExecutor( this );
        }
    }
}
