package com.enonic.wem.core.search.elastic;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.search.IndexData;
import com.enonic.wem.core.search.IndexException;
import com.enonic.wem.core.search.IndexStatus;

@Component
public class ElasticsearchIndexServiceImpl
    implements ElasticsearchIndexService
{
    private Client client;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexServiceImpl.class );

    // TODO: As properties
    private TimeValue WAIT_FOR_YELLOW_TIMEOUT = TimeValue.timeValueSeconds( 20 );

    public static final TimeValue CLUSTER_NOWAIT_TIMEOUT = TimeValue.timeValueSeconds( 1 );

    public IndexSettingsBuilder indexSettingsBuilder;

    @Override
    public IndexStatus getIndexStatus( final String indexName, final boolean waitForStatusYellow )
    {
        final ClusterHealthResponse clusterHealth = getClusterHealth( indexName, waitForStatusYellow );

        LOG.info( "Cluster in state: " + clusterHealth.status().toString() );

        return IndexStatus.valueOf( clusterHealth.getStatus().name() );
    }

    @Override
    public boolean indexExists( String indexName )
    {
        final IndicesExistsResponse exists = this.client.admin().indices().exists( new IndicesExistsRequest( indexName ) ).actionGet();
        return exists.exists();
    }

    public void index( IndexData indexData )
    {

        final XContentBuilder data = indexData.getData();
        final String id = indexData.getId();

        final IndexRequest req =
            Requests.indexRequest().id( id ).index( indexData.getIndexName() ).type( indexData.getIndexType().getIndexTypeName() ).source(
                data );

        this.client.index( req ).actionGet();
    }

    @Override
    public void createIndex( String indexName )
    {
        LOG.debug( "creating index: " + indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( indexSettingsBuilder.buildIndexSettings() );

        try
        {
            client.admin().indices().create( createIndexRequest ).actionGet();
        }
        catch ( ElasticSearchException e )
        {
            throw new IndexException( "Failed to create index:" + indexName, e );
        }

        LOG.info( "Created index: " + indexName );
    }

    @Override
    public void putMapping( final IndexMapping indexMapping )
    {
        final String indexName = indexMapping.getIndexName();
        final String indexType = indexMapping.getIndexType();
        final String source = indexMapping.getSource();

        Preconditions.checkNotNull( indexName );
        Preconditions.checkNotNull( indexType );
        Preconditions.checkNotNull( source );

        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType ).source( source );

        try
        {
            this.client.admin().indices().putMapping( mappingRequest ).actionGet();
        }
        catch ( ElasticSearchException e )
        {
            throw new IndexException( "Failed to apply mapping to index: " + indexName, e );
        }

        LOG.info( "Mapping for index " + indexName + ", index-type: " + indexType + " deleted" );


    }

    private ClusterHealthResponse getClusterHealth( String indexName, boolean waitForYellow )
    {
        LOG.debug( "Testing debug logging level here!" );

        ClusterHealthRequest request = new ClusterHealthRequest( indexName );

        if ( waitForYellow )
        {
            request.waitForYellowStatus().timeout( WAIT_FOR_YELLOW_TIMEOUT );
        }
        else
        {
            request.timeout( CLUSTER_NOWAIT_TIMEOUT );
        }

        final ClusterHealthResponse clusterHealthResponse = this.client.admin().cluster().health( request ).actionGet();

        if ( clusterHealthResponse.timedOut() )
        {
            LOG.warn( "ElasticSearch cluster health timed out" );
        }
        else
        {
            LOG.trace( "ElasticSearch cluster health: Status " + clusterHealthResponse.status().name() + "; " +
                           clusterHealthResponse.getNumberOfNodes() + " nodes; " + clusterHealthResponse.getActiveShards() +
                           " active shards." );
        }

        return clusterHealthResponse;
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }

    @Autowired
    public void setIndexSettingsBuilder( final IndexSettingsBuilder indexSettingsBuilder )
    {
        this.indexSettingsBuilder = indexSettingsBuilder;
    }
}
