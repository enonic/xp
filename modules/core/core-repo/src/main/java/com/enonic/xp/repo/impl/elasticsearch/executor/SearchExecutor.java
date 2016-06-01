package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;

import com.enonic.xp.repo.impl.elasticsearch.ScanAndScrollExecutor;
import com.enonic.xp.repo.impl.elasticsearch.SearchRequestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class SearchExecutor
    extends AbstractExecutor
{
    private SearchExecutor( final Builder builder )
    {
        super( builder );
    }

    public SearchResult search( final ElasticsearchQuery query )
    {
        if ( query.getSearchType().equals( SearchType.SCAN ) )
        {
            return new ScanAndScrollExecutor( this.client ).execute( query );
        }

        final SearchRequestBuilder searchRequest = SearchRequestBuilderFactory.newFactory().
            query( query ).
            client( this.client ).
            resolvedSize( resolveSize( query ) ).
            build().
            create();

        //System.out.println( "######################\n\r" + searchRequest.toString() );

        return doSearchRequest( searchRequest, query.getSearchType() );
    }


    private int resolveSize( final ElasticsearchQuery query )
    {
        if ( query.getSize() == SearchService.GET_ALL_SIZE_FLAG )
        {
            return safeLongToInt( CountExecutor.create( this.client ).
                build().
                count( query ) );
        }
        else
        {
            return query.getSize();
        }
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
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
