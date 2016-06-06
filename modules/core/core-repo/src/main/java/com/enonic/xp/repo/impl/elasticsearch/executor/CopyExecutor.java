package com.enonic.xp.repo.impl.elasticsearch.executor;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;

public class CopyExecutor
    extends AbstractExecutor
{
    private static final TimeValue defaultScrollTime = new TimeValue( 60, TimeUnit.SECONDS );

    private final String targetType;

    private final String targetIndex;

    private final ElasticsearchQuery query;

    private CopyExecutor( final Builder builder )
    {
        super( builder );
        targetType = builder.targetType;
        targetIndex = builder.targetIndex;
        query = builder.query;
    }

    public static Builder create( final Client client )
    {
        return new Builder( client );
    }

    public void execute()
    {
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexName() ).
            setTypes( query.getIndexType() ).
            setScroll( defaultScrollTime ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( query.getBatchSize() ).
            addFields( query.getReturnFields().getReturnFieldNames() );

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

        final ImmutableSet<SortBuilder> sortBuilders = query.getSortBuilders();

        SearchType searchType;

        if ( sortBuilders.isEmpty() )
        {
            searchType = SearchType.SCAN;
        }
        else
        {
            searchType = SearchType.DEFAULT;

            for ( final SortBuilder sortBuilder : sortBuilders )
            {
                searchRequestBuilder.addSort( sortBuilder );
            }
        }

        SearchResponse scrollResp = searchRequestBuilder.
            setSearchType( searchType ).
            execute().
            actionGet();

        while ( true )
        {
            System.out.println( "Fetched [" + scrollResp.getHits().hits().length + "] hits, processing" );

            if ( scrollResp.getHits().getHits().length > 0 )
            {
                doCopy( scrollResp );
            }

            scrollResp = client.prepareSearchScroll( scrollResp.getScrollId() ).
                setScroll( defaultScrollTime ).
                execute().
                actionGet();

            if ( scrollResp.getHits().getHits().length == 0 )
            {
                break;
            }
        }

    }

    private void doCopy( final SearchResponse scrollResp )
    {
        BulkRequestBuilder bulkRequest = client.prepareBulk();

        final org.elasticsearch.search.SearchHits hits = scrollResp.getHits();

        for ( final org.elasticsearch.search.SearchHit hit : hits )
        {
            bulkRequest.add( Requests.indexRequest().
                id( hit.id() ).
                index( this.targetIndex ).
                type( this.targetType ).
                source( hit.source() ).
                refresh( false ) );
        }

        final Stopwatch timer = Stopwatch.createStarted();
        final BulkResponse response = bulkRequest.execute().actionGet();
        System.out.println( "Copied [" + response.getItems().length + "] in " + timer.stop() );
    }


    public static final class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        private String targetType;

        private String targetIndex;

        private NodeIds nodeIds;

        private ElasticsearchQuery query;

        private Builder( final Client client )
        {
            super( client );
        }

        public Builder targetType( final String val )
        {
            targetType = val;
            return this;
        }

        public Builder targetIndex( final String val )
        {
            targetIndex = val;
            return this;
        }

        public Builder nodeIds( final NodeIds val )
        {
            nodeIds = val;
            return this;
        }

        public Builder query( final ElasticsearchQuery val )
        {
            query = val;
            return this;
        }

        public CopyExecutor build()
        {
            return new CopyExecutor( this );
        }
    }
}
