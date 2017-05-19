package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchHitsFactory;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class ScrollExecutor
    extends AbstractExecutor
{
    private static final TimeValue defaultScrollTime = new TimeValue( 30, TimeUnit.SECONDS );

    private final static Logger LOG = LoggerFactory.getLogger( ScrollExecutor.class );

    private ScrollExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public SearchResult execute( final ElasticsearchQuery query )
    {
        final SearchRequestBuilder searchRequestBuilder = createScrollRequest( query );

        SearchResponse scrollResp = searchRequestBuilder.
            execute().
            actionGet();

        final SearchHits.Builder searchHitsBuilder = SearchHits.create();

        while ( true )
        {
            LOG.debug( "Scrolling, got " + scrollResp.getHits().hits().length + " hits" );

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

        return SearchResult.create().
            hits( searchHitsBuilder.build() ).
            totalHits( scrollResp.getHits().getTotalHits() ).
            maxScore( scrollResp.getHits().maxScore() ).
            build();
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final Client client )
        {
            super( client );
        }

        public ScrollExecutor build()
        {
            return new ScrollExecutor( this );
        }
    }
}
