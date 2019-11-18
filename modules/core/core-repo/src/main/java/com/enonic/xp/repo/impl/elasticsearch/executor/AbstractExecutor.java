package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;

import com.enonic.xp.repo.impl.elasticsearch.SearchRequestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchResultFactory;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.IndexException;

abstract class AbstractExecutor
{
    static final TimeValue DEFAULT_SCROLL_TIME = new TimeValue( 60, TimeUnit.SECONDS );

    final String storeTimeout = "10s";

    final String deleteTimeout = "5s";

    final RestHighLevelClient client;

    private final ExecutorProgressListener progressReporter;

    final String searchPreference = "_local";

    private final String searchTimeout = "30s";

    AbstractExecutor( final Builder builder )
    {
        client = builder.client;
        progressReporter = builder.progressReporter;
    }

    static int safeLongToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( l + " cannot be cast to int without changing its value." );
        }
        return (int) l;
    }

    SearchResult doSearchRequest( final org.elasticsearch.action.search.SearchRequest searchRequest )
    {
        try
        {
            final SearchResponse searchResponse = client.search( searchRequest, RequestOptions.DEFAULT );

            return SearchResultFactory.create( searchResponse );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException(
                "Search request failed after [" + this.searchTimeout + "], query: [" + createQueryString( searchRequest ) + "]", e );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private String createQueryString( final SearchRequest searchRequestBuilder )
    {
        final String queryAsString = searchRequestBuilder.toString();

        if ( queryAsString.length() > 5000 )
        {
            return queryAsString.substring( 0, 5000 ) + "....(more)";
        }

        return queryAsString;
    }

    SearchRequest createScrollRequest( final ElasticsearchQuery query )
    {
        return createScrollRequest( query, true );
    }

    SearchRequest createScrollRequest( final ElasticsearchQuery query, final boolean discoverFromParam )
    {
        return SearchRequestBuilderFactory.newFactory().
            query( query ).
            scrollTimeout( DEFAULT_SCROLL_TIME.getStringRep() ).
            resolvedSize( query.getBatchSize() ).
            build().
            create( discoverFromParam );
    }

    void clearScroll( final SearchResponse scrollResp )
    {
        final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId( scrollResp.getScrollId() );

        try
        {
            client.clearScroll( clearScrollRequest, RequestOptions.DEFAULT );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
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
        private final RestHighLevelClient client;

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

        Builder( final RestHighLevelClient client )
        {
            this.client = client;
        }
    }
}
