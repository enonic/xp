package com.enonic.xp.repo.impl.elasticsearch.executor;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;

import com.enonic.xp.repo.impl.elasticsearch.SearchRequestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.result.SearchResult;

class CountExecutor
    extends AbstractExecutor
{

    private CountExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final RestHighLevelClient client )
    {
        return new Builder( client );
    }

    public SearchResult execute( final ElasticsearchQuery query )
    {
        final SearchRequest searchRequest = SearchRequestBuilderFactory.newFactory().
            query( query ).
            preference( searchPreference ).
            build().
            create();

        return doSearchRequest( searchRequest );
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private Builder( final RestHighLevelClient client )
        {
            super( client );
        }


        public CountExecutor build()
        {
            return new CountExecutor( this );
        }
    }
}
