package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.MultiSearchAction;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

import com.google.common.base.Throwables;

import com.enonic.xp.repo.impl.elasticsearch.result.SearchResultFactory;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.IndexException;

abstract class AbstractExecutor
{
    static final TimeValue DEFAULT_SCROLL_TIME = new TimeValue( 60, TimeUnit.SECONDS );

    final String storeTimeout = "10s";

    final String deleteTimeout = "5s";

    final Client client;

    private final ExecutorProgressListener progressReporter;

    private final String searchTimeout = "30s";

    AbstractExecutor( final Builder builder )
    {
        client = builder.client;
        progressReporter = builder.progressReporter;
    }

    SearchResult doSearchRequest( final SearchRequestBuilder searchRequestBuilder )
    {
        try
        {
            final SearchResponse searchResponse = searchRequestBuilder.execute().actionGet( searchTimeout );

            return SearchResultFactory.create( searchResponse );
        }
        catch ( ElasticsearchException e )
        {
            throw rethrowException( e, searchRequestBuilder );
        }
    }

    List<SearchResult> doSearchRequests( final SearchRequestBuilder... searchRequestBuilders )
    {
        List<SearchResult> results = new ArrayList<>();
        final MultiSearchRequestBuilder multiSearchRequestBuilder = MultiSearchAction.INSTANCE.newRequestBuilder( client );

        for ( SearchRequestBuilder searchRequestBuilder : searchRequestBuilders )
        {
            multiSearchRequestBuilder.add( searchRequestBuilder );
        }

        final MultiSearchResponse.Item[] searchResponses = multiSearchRequestBuilder.execute().actionGet( searchTimeout ).getResponses();

        for ( int i = 0; i < searchResponses.length; i++ )
        {
            MultiSearchResponse.Item searchResponse = searchResponses[i];

            if ( searchResponse.isFailure() )
            {
                throw rethrowException( searchResponse.getFailure(), searchRequestBuilders[i] );
            }
            results.add( SearchResultFactory.create( searchResponse.getResponse() ) );
        }
        return results;
    }

    private RuntimeException rethrowException( Throwable throwable, SearchRequestBuilder searchRequestBuilder )
    {
        if ( throwable instanceof ElasticsearchTimeoutException )
        {
            throw new IndexException(
                "Search request failed after [" + this.searchTimeout + "], query: [" + createQueryString( searchRequestBuilder ) + "]",
                (ElasticsearchTimeoutException) throwable );
        }
        else if ( throwable instanceof ElasticsearchException )
        {
            throw new IndexException( "Search request failed, query: [" + createQueryString( searchRequestBuilder ) + "]",
                                      (ElasticsearchException) throwable );

        }
        else
        {
            Throwables.throwIfUnchecked( throwable );
            throw new RuntimeException( throwable );
        }
    }

    private String createQueryString( final SearchRequestBuilder searchRequestBuilder )
    {
        final String queryAsString = searchRequestBuilder.toString();

        if ( queryAsString.length() > 5000 )
        {
            return queryAsString.substring( 0, 5000 ) + "....(more)";
        }

        return queryAsString;
    }

    void clearScroll( final SearchResponse scrollResp )
    {
        final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId( scrollResp.getScrollId() );

        client.clearScroll( clearScrollRequest ).actionGet();
    }

    void reportProgress( final int count )
    {
        if ( progressReporter != null )
        {
            progressReporter.progress( count );
        }
    }

    public static class Builder<B extends Builder>
    {
        private final Client client;

        private ExecutorProgressListener progressReporter;

        public B progressReporter( final ExecutorProgressListener progressReporter )
        {
            this.progressReporter = progressReporter;
            return typecastThis();
        }

        @SuppressWarnings("unchecked")
        private B typecastThis()
        {
            return (B) this;
        }

        Builder( final Client client )
        {
            this.client = client;
        }
    }
}
