package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Collection;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.client.Client;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.elasticsearch.document.DeleteDocument;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.index.IndexException;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.index.IndexSettings;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;


@Component
public class ElasticsearchIndexServiceInternal
    implements IndexServiceInternal
{

    private static final String ES_DEFAULT_INDEX_TYPE_NAME = "_default_";

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexServiceInternal.class );

    private static final String INDICES_RESPONSE_TIMEOUT = "10s";

    private final static String DELETE_TIMEOUT = "5s";

    private final static String CREATE_TIMEOUT = "5s";

    private final static String UPDATE_TIMEOUT = "5s";

    private final static String APPLY_MAPPING_TIMEOUT = "5s";

    private final static String EXISTS_TIMEOUT = "5s";

    private final static String CLUSTER_STATE_TIMEOUT = "5s";

    private ElasticsearchDao elasticsearchDao;

    private Client client;

    @Override
    public ClusterHealthStatus getClusterHealth( final String timeout, final String... indexNames )
    {
        return doGetClusterHealth( timeout, indexNames );
    }

    @Override
    public void refresh( final String... indexNames )
    {
        client.admin().indices().prepareRefresh( indexNames ).execute().actionGet();
    }

    @Override
    public boolean isMaster()
    {
        final ClusterStateRequestBuilder requestBuilder = new ClusterStateRequestBuilder( this.client.admin().cluster() ).
            setBlocks( false ).
            setIndices().
            setBlocks( false ).
            setMetaData( false ).
            setNodes( true ).
            setRoutingTable( false );

        final ClusterStateResponse clusterStateResponse =
            client.admin().cluster().state( requestBuilder.request() ).actionGet( CLUSTER_STATE_TIMEOUT );

        return clusterStateResponse.getState().nodes().localNodeMaster();
    }

    @Override
    public void createIndex( final String indexName, final IndexSettings settings )
    {
        LOG.info( "creating index {}", indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( settings.getSettingsAsString() );

        try
        {
            final CreateIndexResponse createIndexResponse = client.admin().
                indices().
                create( createIndexRequest ).
                actionGet( CREATE_TIMEOUT );

            LOG.info( "Index {} created with status {}", indexName, createIndexResponse.isAcknowledged() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to create index: " + indexName, e );
        }
    }

    @Override
    public void updateIndex( final String indexName, final IndexSettings settings )
    {
        LOG.info( "updating index {}", indexName );

        final UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest().
            indices( indexName ).
            settings( settings.getSettingsAsString() );
        try
        {
            final UpdateSettingsResponse updateSettingsResponse = client.admin().
                indices().
                updateSettings( updateSettingsRequest ).
                actionGet( UPDATE_TIMEOUT );

            LOG.info( "Index {} updated with status {}", indexName, updateSettingsResponse.isAcknowledged() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to update index: " + indexName, e );
        }
    }

    @Override
    public void applyMapping( final String indexName, final IndexType indexType, final String mapping )
    {
        LOG.info( "Apply mapping for index {}", indexName );

        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).
            type( indexType.equals( IndexType.SEARCH ) ? ES_DEFAULT_INDEX_TYPE_NAME : indexType.getName() ).
            source( mapping );

        try
        {
            this.client.admin().
                indices().
                putMapping( mappingRequest ).
                actionGet( APPLY_MAPPING_TIMEOUT );

            LOG.info( "Mapping for index {} applied", indexName );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to apply mapping to index: " + indexName, e );
        }
    }

    @Override
    public void deleteIndices( String... indexNames )
    {
        for ( final String indexName : indexNames )
        {
            doDeleteIndex( indexName );
        }
    }

    @Override
    public boolean indicesExists( final String... indices )
    {
        IndicesExistsRequest request = new IndicesExistsRequestBuilder( this.client.admin().indices() ).setIndices( indices ).request();

        final IndicesExistsResponse response = client.admin().indices().exists( request ).actionGet( EXISTS_TIMEOUT );

        return response.isExists();
    }

    private ClusterHealthStatus doGetClusterHealth( final String timeout, final String... indexNames )
    {
        LOG.info( "Executing ClusterHealtRequest" );

        ClusterHealthRequest request = indexNames != null ? new ClusterHealthRequest( indexNames ) : new ClusterHealthRequest();

        request.waitForYellowStatus().timeout( timeout );

        final Stopwatch timer = Stopwatch.createStarted();
        final ClusterHealthResponse response = this.client.admin().cluster().health( request ).actionGet();
        timer.stop();

        LOG.info(
            "ElasticSearch cluster '{}' health (timedOut={}, timeOutValue={}, used={}): Status={}, nodes={}, active shards={}, indices={}",
            response.getClusterName(), response.isTimedOut(), timeout, timer.toString(), response.getStatus(), response.getNumberOfNodes(),
            response.getActiveShards(), response.getIndices().keySet() );

        return new ClusterHealthStatus( ClusterStatusCode.valueOf( response.getStatus().name() ), response.isTimedOut() );
    }


    private void doDeleteIndex( final String indexName )
    {
        final DeleteIndexRequest req = new DeleteIndexRequest( indexName );

        try
        {
            client.admin().indices().delete( req ).actionGet( DELETE_TIMEOUT );
            LOG.info( "Deleted index {}", indexName );
        }
        catch ( ElasticsearchException e )
        {
            LOG.warn( "Failed to delete index {}", indexName );
        }
    }

    @Override
    public void store( final Node node, final InternalContext context )
    {
        final Collection<IndexDocument> indexDocuments = NodeStoreDocumentFactory.createBuilder().
            node( node ).
            branch( context.getBranch() ).
            repositoryId( context.getRepositoryId() ).
            build().
            create();

        elasticsearchDao.store( indexDocuments );
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() );
        final String indexType = context.getBranch().getName();

        elasticsearchDao.delete( DeleteDocument.create().
            indexName( indexName ).
            indexTypeName( indexType ).
            id( nodeId.toString() ).
            build() );
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
