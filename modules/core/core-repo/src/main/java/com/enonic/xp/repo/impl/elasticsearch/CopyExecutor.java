package com.enonic.xp.repo.impl.elasticsearch;

import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.unit.TimeValue;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;

public class CopyExecutor
{
    private final Client client;

    private final String targetType;

    private final String targetIndex;

    private final NodeIds nodeIds;

    private static final TimeValue defaultScrollTime = new TimeValue( 60, TimeUnit.SECONDS );

    private final ElasticsearchQuery query;

    private CopyExecutor( final Builder builder )
    {
        client = builder.client;
        targetType = builder.targetType;
        targetIndex = builder.targetIndex;
        nodeIds = builder.nodeIds;
        query = builder.query;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {

        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexName() ).
            setTypes( query.getIndexType() );

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

        SearchResponse scrollResp = searchRequestBuilder.
            setScroll( defaultScrollTime ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( query.getBatchSize() ).
            addFields( query.getReturnFields().getReturnFieldNames() ).
            execute().
            actionGet();

        while ( true )
        {
            doCopy( scrollResp );

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

        bulkRequest.execute().actionGet();
    }


    public static final class Builder
    {
        private Client client;

        private String targetType;

        private String targetIndex;

        private NodeIds nodeIds;

        private ElasticsearchQuery query;

        private Builder()
        {
        }

        public Builder client( final Client val )
        {
            client = val;
            return this;
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
