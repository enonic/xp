package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import com.enonic.xp.node.SearchMode;
import com.enonic.xp.repo.impl.elasticsearch.SearchRequestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.aggregation.AggregationsFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.ESQueryTranslator;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchHitsFactory;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchResultFactory;
import com.enonic.xp.repo.impl.elasticsearch.suggistion.SuggestionsFactory;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repository.IndexException;

public class SearchExecutor
    extends AbstractExecutor
{
    private static final String SEARCH_TIMEOUT = "30s";

    private static final Logger LOG = LoggerFactory.getLogger( SearchExecutor.class );

    private SearchExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public SearchResult execute( final SearchRequest searchRequest )
    {
        final ElasticsearchQuery query = ESQueryTranslator.translate( searchRequest );

        if (  SearchMode.COUNT == query.getSearchMode() )
        {
            return count( query );
        }
        else if ( NodeSearchService.GET_ALL_SIZE_FLAG == query.getSize() )
        {
            return scroll( query );
        }
        else
        {
            return search( query );
        }
    }

    private SearchResult count( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequestBuilder = SearchRequestBuilderFactory.newFactory()
            .query( query )
            .client( this.client )
            .searchPreference( query.getSearchPreference() )
            .build()
            .createCountRequest();

        return doSearchRequest( searchRequestBuilder );
    }

    private SearchResult search( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequestBuilder = SearchRequestBuilderFactory.newFactory()
            .query( query )
            .client( this.client )
            .searchPreference( query.getSearchPreference() )
            .build()
            .createSearchRequest();

        return doSearchRequest( searchRequestBuilder );
    }

    private SearchResult scroll( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequestBuilder = SearchRequestBuilderFactory.newFactory()
            .query( query )
            .client( this.client )
            .build()
            .createScrollRequest( DEFAULT_SCROLL_TIME );

        SearchResponse scrollResp = searchRequestBuilder.execute().actionGet();

        final SearchResponse initialResponse = scrollResp;

        final SearchHits.Builder searchHitsBuilder = SearchHits.create();

        do
        {
            LOG.debug( "Scrolling, got {} hits", scrollResp.getHits().hits().length );

            searchHitsBuilder.addAll( SearchHitsFactory.create( scrollResp.getHits() ) );

            scrollResp = client.prepareSearchScroll( scrollResp.getScrollId() ).setScroll( DEFAULT_SCROLL_TIME ).execute().actionGet();

        }
        while ( 0 != scrollResp.getHits().hits().length );

        clearScroll( scrollResp.getScrollId() );

        return SearchResult.create().
            hits( searchHitsBuilder.build() ).
            totalHits( scrollResp.getHits().getTotalHits() ).
            maxScore( scrollResp.getHits().maxScore() ).
            aggregations( AggregationsFactory.create( initialResponse.getAggregations() ) ).
            suggestions( SuggestionsFactory.create( initialResponse.getSuggest() ) ).
            build();
    }

    SearchResult doSearchRequest( final SearchRequestBuilder searchRequestBuilder )
    {
        try
        {
            final SearchResponse searchResponse = searchRequestBuilder.execute().actionGet( SEARCH_TIMEOUT );

            return SearchResultFactory.create( searchResponse );
        }
        catch ( ElasticsearchException e )
        {
            throw rethrowException( e, searchRequestBuilder );
        }
    }

    private RuntimeException rethrowException( Throwable throwable, SearchRequestBuilder searchRequestBuilder )
    {
        if ( throwable instanceof ElasticsearchTimeoutException )
        {
            throw new IndexException(
                "Search request failed after [" + SEARCH_TIMEOUT + "], query: [" + createQueryString( searchRequestBuilder ) + "]",
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

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final Client client )
        {
            super( client );
        }

        public SearchExecutor build()
        {
            return new SearchExecutor( this );
        }
    }
}
