package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.SearchHitsFactory;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class ScrollExecutor
    extends AbstractExecutor
{
    private static final TimeValue DEFAULT_SCROLL_TIME = new TimeValue( 30, TimeUnit.SECONDS );

    private final static Logger LOG = LoggerFactory.getLogger( ScrollExecutor.class );

    private ScrollExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final EsClient client )
    {
        return new Builder( client );
    }

    public SearchResult execute( final ElasticsearchQuery query )
    {
        final SearchRequest searchRequest = createScrollRequest( query );

        SearchResponse scrollResp = client.search( searchRequest );

        final SearchHits.Builder searchHitsBuilder = SearchHits.create();

        while ( true )
        {
            LOG.debug( "Scrolling, got " + scrollResp.getHits().getHits().length + " hits" );

            searchHitsBuilder.addAll( SearchHitsFactory.create( scrollResp.getHits() ) );

            final SearchScrollRequest searchScrollRequest = new SearchScrollRequest( scrollResp.getScrollId() ).
                scroll( DEFAULT_SCROLL_TIME );

            scrollResp = client.scroll( searchScrollRequest );

            if ( scrollResp.getHits().getHits().length == 0 )
            {
                clearScroll( scrollResp );
                break;
            }
        }

        return SearchResult.create().
            hits( searchHitsBuilder.build() ).
            totalHits( scrollResp.getHits().getTotalHits().value ).
            maxScore( scrollResp.getHits().getMaxScore() ).
            build();
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final EsClient client )
        {
            super( client );
        }

        public ScrollExecutor build()
        {
            return new ScrollExecutor( this );
        }
    }
}
