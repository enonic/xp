package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;

import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class CountExecutor
    extends AbstractExecutor
{

    private CountExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public SearchResult execute( final ElasticsearchQuery query )
    {
        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder( this.client, SearchAction.INSTANCE ).
            setIndices( query.getIndexNames() ).
            setTypes( query.getIndexTypes() ).
            setQuery( query.getQuery() ).
            setSearchType( SearchType.COUNT ).
            setPreference( searchPreference );

        return doSearchRequest( searchRequestBuilder );
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final Client client )
        {
            super( client );
        }


        public CountExecutor build()
        {
            return new CountExecutor( this );
        }
    }
}
