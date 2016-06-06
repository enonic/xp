package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchHitsFactory;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class ScanAndScrollExecutor
{
    private static final TimeValue defaultScrollTime = new TimeValue( 30, TimeUnit.SECONDS );

    private final static Logger LOG = LoggerFactory.getLogger( ScanAndScrollExecutor.class );

    private final Client client;

    public ScanAndScrollExecutor( final Client client )
    {
        this.client = client;
    }

    public SearchResult execute( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexName() ).
            setTypes( query.getIndexType() ).
            setScroll( defaultScrollTime ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( query.getBatchSize() ).
            addFields( query.getReturnFields().getReturnFieldNames() );

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

        SearchResponse scrollResp = searchRequestBuilder.
            setSearchType( searchType ).
            execute().
            actionGet();

        final SearchHits.Builder searchHitsBuilder = SearchHits.create().
            totalHits( scrollResp.getHits().totalHits() );

        while ( true )
        {
            System.out.println( "Scrolling [" + searchType + "] , got " + scrollResp.getHits().hits().length + " hits" );

            searchHitsBuilder.addAll( SearchHitsFactory.create( scrollResp.getHits() ) );

            scrollResp = client.prepareSearchScroll( scrollResp.getScrollId() ).
                setScroll( defaultScrollTime ).
                execute().
                actionGet();

            if ( scrollResp.getHits().getHits().length == 0 )
            {
                clearScroll( scrollResp );
                break;
            }
        }

        final SearchHits build = searchHitsBuilder.build();

        return SearchResult.create().
            hits( build ).
            build();
    }

    private void clearScroll( final SearchResponse scrollResp )
    {
        final ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId( scrollResp.getScrollId() );

        client.clearScroll( clearScrollRequest ).actionGet();
    }

}
