package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchResultFactory;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public abstract class AbstractExecutor
{
    protected static final TimeValue defaultScrollTime = new TimeValue( 60, TimeUnit.SECONDS );

    protected final String storeTimeout = "10s";

    protected final String deleteTimeout = "5s";

    protected final Client client;

    final String searchPreference = "_local";

    private final String searchTimeout = "10s";

    protected AbstractExecutor( final Builder builder )
    {
        client = builder.client;
    }

    static int safeLongToInt( long l )
    {
        if ( l < Integer.MIN_VALUE || l > Integer.MAX_VALUE )
        {
            throw new IllegalArgumentException( l + " cannot be cast to int without changing its value." );
        }
        return (int) l;
    }

    SearchResult doSearchRequest( final SearchRequestBuilder searchRequestBuilder )
    {
        try
        {
            final SearchResponse searchResponse = searchRequestBuilder.
                setPreference( searchPreference ).
                execute().
                actionGet( searchTimeout );

            return SearchResultFactory.create( searchResponse );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException(
                "Search request failed after [" + this.searchTimeout + "], query: [" + createQueryString( searchRequestBuilder ) + "]", e );
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

    protected SearchRequestBuilder createScrollRequest( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexName() ).
            setTypes( query.getIndexType() ).
            setScroll( defaultScrollTime ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( query.getBatchSize() ).
            addFields( query.getReturnFields().getReturnFieldNames() );

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

        final ImmutableSet<SortBuilder> sortBuilders = query.getSortBuilders();

        SearchType searchType;

        if ( sortBuilders.isEmpty() )
        {
            searchType = SearchType.SCAN;
        }
        else
        {
            searchType = SearchType.DEFAULT;

            for ( final SortBuilder sortBuilder : sortBuilders )
            {
                searchRequestBuilder.addSort( sortBuilder );
            }
        }

        searchRequestBuilder.
            setSearchType( searchType );
        return searchRequestBuilder;
    }

    protected void clearScroll( final SearchResponse scrollResp )
    {
        final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId( scrollResp.getScrollId() );

        client.clearScroll( clearScrollRequest ).actionGet();
    }

    public static class Builder<B extends Builder>
    {
        private final Client client;

        protected Builder( final Client client )
        {
            this.client = client;
        }
    }
}
