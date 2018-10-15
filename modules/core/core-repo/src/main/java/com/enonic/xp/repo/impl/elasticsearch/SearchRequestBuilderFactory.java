package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;

public class SearchRequestBuilderFactory
{
    private final int resolvedSize;

    private final ElasticsearchQuery query;

    private final Client client;

    private SearchRequestBuilderFactory( final Builder builder )
    {
        resolvedSize = builder.resolvedSize;
        query = builder.query;
        client = builder.client;
    }

    public static Builder newFactory()
    {
        return new Builder();
    }

    public SearchRequestBuilder create()
    {
        final SearchType searchType =
            query.getSearchOptimizer().equals( SearchOptimizer.ACCURACY ) ? SearchType.DFS_QUERY_THEN_FETCH : SearchType.DEFAULT;

        final SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder( this.client, SearchAction.INSTANCE ).
            setExplain( query.isExplain() ).
            setIndices( query.getIndexNames() ).
            setTypes( query.getIndexTypes() ).
            setSearchType( searchType ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( resolvedSize );

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

        query.getAggregations().forEach( searchRequestBuilder::addAggregation );

        if ( query.getReturnFields() != null && query.getReturnFields().isNotEmpty() )
        {
            searchRequestBuilder.addFields( query.getReturnFields().getReturnFieldNames() );
        }

        return searchRequestBuilder;
    }


    public static final class Builder
    {
        private int resolvedSize;

        private ElasticsearchQuery query;

        private Client client;

        private Builder()
        {
        }

        public Builder resolvedSize( final int resolvedSize )
        {
            this.resolvedSize = resolvedSize;
            return this;
        }

        public Builder query( final ElasticsearchQuery query )
        {
            this.query = query;
            return this;
        }

        public Builder client( final Client client )
        {
            this.client = client;
            return this;
        }

        public SearchRequestBuilderFactory build()
        {
            return new SearchRequestBuilderFactory( this );
        }
    }
}
