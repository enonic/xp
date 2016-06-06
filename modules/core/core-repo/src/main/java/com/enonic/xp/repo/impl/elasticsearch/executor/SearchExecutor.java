package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.node.SearchMode;
import com.enonic.xp.repo.impl.elasticsearch.SearchRequestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class SearchExecutor
    extends AbstractExecutor
{

    private static final int SCAN_THRESHOLD = 1000;

    private final static Logger LOG = LoggerFactory.getLogger( SearchExecutor.class );

    private SearchExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public SearchResult search( final ElasticsearchQuery query )
    {
        final SearchMode searchMode = query.getSearchMode();
        final int size = query.getSize();
        final boolean anyOrderExpressions = !query.getSortBuilders().isEmpty();
        final boolean anyAggregations = !query.getAggregations().isEmpty();

        if ( searchMode.equals( SearchMode.COUNT ) )
        {
            return CountExecutor.create( this.client ).
                build().
                count( query );
        }

        if ( size == SearchService.GET_ALL_SIZE_FLAG || size > SCAN_THRESHOLD )
        {
            if ( anyAggregations ) // || anyOrderExpressions)
            {
                // LOG.warn( "Query with size [" + query.getSize() + "]     > threshold [" + this.SCAN_THRESHOLD +
                //               "] but with aggregations or orderExpressions, may be slow: " );
            }
            else
            {
                return new ScanAndScrollExecutor( this.client ).execute( query );
            }
        }

        final SearchRequestBuilder searchRequest = SearchRequestBuilderFactory.newFactory().
            query( query ).
            client( this.client ).
            resolvedSize( resolveSize( query ) ).
            build().
            create();

        //System.out.println( "######################\n\r" + searchRequest.toString() );

        return doSearchRequest( searchRequest );
    }

    private int resolveSize( final ElasticsearchQuery query )
    {
        if ( query.getSize() == SearchService.GET_ALL_SIZE_FLAG )
        {
            final SearchResult countResult = CountExecutor.create( this.client ).
                build().
                count( query );

            return safeLongToInt( countResult.getResults().getTotalHits() );
        }
        else
        {
            return query.getSize();
        }
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
