package com.enonic.wem.repo.internal.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;

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
        final SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder( this.client ).
            setIndices( query.getIndexName() ).
            setTypes( query.getIndexType() ).
            setSearchType( SearchType.DEFAULT ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( resolvedSize );

        for ( final SortBuilder sortBuilder : query.getSortBuilders() )
        {
            searchRequestBuilder.addSort( sortBuilder );
        }

        for ( final AbstractAggregationBuilder aggregationBuilder : query.getAggregations() )
        {
            searchRequestBuilder.addAggregation( aggregationBuilder );
        }

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
