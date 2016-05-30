package com.enonic.xp.repo.impl.elasticsearch;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchHitsFactory;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class ScanAndScrollExecutor
{
    private final Client client;

    private static final TimeValue defaultScrollTime = new TimeValue( 60, TimeUnit.SECONDS );

    public ScanAndScrollExecutor( final Client client )
    {
        this.client = client;
    }

    SearchResult execute( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexName() ).
            setTypes( query.getIndexType() );

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

        SearchResponse scrollResp = searchRequestBuilder.
            setScroll( defaultScrollTime ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( query.getBatchSize() ).
            addFields( query.getReturnFields().getReturnFieldNames() ).
            execute().
            actionGet();

        final SearchHits.Builder searchHitsBuilder = SearchHits.create().
            totalHits( scrollResp.getHits().totalHits() );

        while ( true )
        {
            searchHitsBuilder.addAll( SearchHitsFactory.create( scrollResp.getHits() ) );

            scrollResp = client.prepareSearchScroll( scrollResp.getScrollId() ).
                setScroll( defaultScrollTime ).
                execute().
                actionGet();

            if ( scrollResp.getHits().getHits().length == 0 )
            {
                break;
            }
        }

        final SearchHits build = searchHitsBuilder.build();

        return SearchResult.create().
            hits( build ).
            build();
    }

}
