package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.Objects;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.node.SearchMode;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.elasticsearch.SearchRequestBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.ESQueryTranslator;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchResult;

public class SearchExecutor
    extends AbstractExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( SearchExecutor.class );

    private SearchExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public SearchResult execute( final SearchRequest searchRequest )
    {
        final ElasticsearchQuery query = ESQueryTranslator.translate( searchRequest );

        final SearchMode searchMode = query.getSearchMode();

        if ( searchMode.equals( SearchMode.COUNT ) )
        {
            return count( query );
        }

        if ( query.getSize() == NodeSearchService.GET_ALL_SIZE_FLAG )
        {
            if ( !query.getAggregations().isEmpty() )
            {
                LOG.debug( "Query with get-all size and aggregations. Scan not possible." );
                final int resolvedSize = Math.toIntExact( count( query ).getTotalHits() );
                return doSearch( query, resolvedSize );
            }
            return ScrollExecutor.create( this.client ).build().execute( query );
        }
        else
        {
            return doSearch( query, query.getSize() );
        }
    }

    public SearchResult count( final ElasticsearchQuery query )
    {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexNames() )
                .setTypes( query.getIndexTypes() )
                .setQuery( query.getQuery() )
                .setSize( 0 )
                .setPreference( Objects.requireNonNullElse( query.getSearchPreference(), SearchPreference.LOCAL ).getName() );

        return doSearchRequest( searchRequestBuilder );
    }

    private SearchResult doSearch( final ElasticsearchQuery query, int size )
    {
        final SearchRequestBuilder searchRequestBuilder = SearchRequestBuilderFactory.newFactory()
            .query( query )
            .client( this.client )
            .resolvedSize( size )
            .searchPreference( query.getSearchPreference() )
            .build()
            .createSearchRequest();

        return doSearchRequest( searchRequestBuilder );
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
