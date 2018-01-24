package com.enonic.xp.repo.impl.elasticsearch;

import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.IndexMissingException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.index.ApplyMappingRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.index.UpdateIndexSettings;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.RepositoryId;


@Component
public class IndexServiceInternalImpl
    implements IndexServiceInternal
{
    private final static Logger LOG = LoggerFactory.getLogger( IndexServiceInternalImpl.class );

    private static final String ES_DEFAULT_INDEX_TYPE_NAME = "_default_";

    private final static String DELETE_INDEX_TIMEOUT = "5s";

    private final static String CREATE_INDEX_TIMEOUT = "5s";

    private final static String UPDATE_INDEX_TIMEOUT = "5s";

    private final static String APPLY_MAPPING_TIMEOUT = "5s";

    private final static String INDEX_EXISTS_TIMEOUT = "5s";

    private final static String CLUSTER_STATE_TIMEOUT = "5s";

    private final static String GET_SETTINGS_TIMEOUT = "5s";

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
    public void copy( final NodeId nodeId, final RepositoryId repositoryId, final Branch source, final Branch target )
    {
        final GetRequest request = new GetRequestBuilder( this.client ).setId( nodeId.toString() ).
            setIndex( IndexNameResolver.resolveSearchIndexName( repositoryId ) ).
            setType( source.getValue() ).
            request();

        final GetResponse response = this.client.get( request ).actionGet();

        if ( !response.isExists() )
        {
            throw new IndexException( "Could not copy entry with id [" + nodeId + "], does not exist" );
        }

        final Map<String, Object> sourceValues = response.getSource();

        final IndexRequest req = Requests.indexRequest().
            id( nodeId.toString() ).
            index( IndexNameResolver.resolveSearchIndexName( repositoryId ) ).
            type( target.getValue() ).
            source( sourceValues ).
            refresh( false );

        this.client.index( req ).actionGet();

    }

    @Override
    public void createIndex( final com.enonic.xp.repo.impl.index.CreateIndexRequest request )
    {
        final String indexName = request.getIndexName();
        final IndexSettings indexSettings = request.getIndexSettings();
        LOG.info( "creating index {}", indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( indexSettings.getAsString() );

        try
        {
            final CreateIndexResponse createIndexResponse = client.admin().
                indices().
                create( createIndexRequest ).
                actionGet( CREATE_INDEX_TIMEOUT );

            LOG.info( "Index {} created with status {}, settings {}", indexName, createIndexResponse.isAcknowledged(),
                      indexSettings.getAsString() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to create index: " + indexName, e );
        }
    }


    @Override
    public void updateIndex( final String indexName, final UpdateIndexSettings settings )
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
                actionGet( UPDATE_INDEX_TIMEOUT );

            LOG.info( "Index {} updated with status {}", indexName, updateSettingsResponse.isAcknowledged() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to update index: " + indexName, e );
        }
    }

    @Override
    public IndexSettings getIndexSettings( final RepositoryId repositoryId, final IndexType indexType )
    {
        if ( repositoryId == null || indexType == null )
        {
            return null;
        }

        final String indexName = IndexType.SEARCH == indexType
            ? IndexNameResolver.resolveSearchIndexName( repositoryId )
            : IndexNameResolver.resolveStorageIndexName( repositoryId );

        final ImmutableOpenMap<String, Settings> settingsMap =
            this.client.admin().indices().getSettings( new GetSettingsRequest().indices( indexName ) ).actionGet(
                GET_SETTINGS_TIMEOUT ).getIndexToSettings();

        return IndexSettings.from( (Map) settingsMap.get( indexName ).getAsMap() );
    }

    @Override
    public void applyMapping( final ApplyMappingRequest request )
    {
        final String indexName = request.getIndexName();
        LOG.info( "Apply mapping for index {}", indexName );

        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).
            type( request.getIndexType().isDynamicTypes() ? ES_DEFAULT_INDEX_TYPE_NAME : request.getIndexType().getName() ).
            source( request.getMapping().getAsString() );

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

        final IndicesExistsResponse response = client.admin().indices().exists( request ).actionGet( INDEX_EXISTS_TIMEOUT );

        return response.isExists();
    }

    private ClusterHealthStatus doGetClusterHealth( final String timeout, final String... indexNames )
    {
        ClusterHealthRequest request = indexNames != null ? new ClusterHealthRequest( indexNames ) : new ClusterHealthRequest();

        request.waitForYellowStatus().timeout( timeout );

        final Stopwatch timer = Stopwatch.createStarted();
        final ClusterHealthResponse response = this.client.admin().cluster().health( request ).actionGet();
        timer.stop();

        LOG.debug(
            "ElasticSearch cluster '{}' health (timedOut={}, timeOutValue={}, used={}): Status={}, nodes={}, active shards={}, indices={}",
            response.getClusterName(), response.isTimedOut(), timeout, timer.toString(), response.getStatus(), response.getNumberOfNodes(),
            response.getActiveShards(), response.getIndices().keySet() );

        return new ClusterHealthStatus( ClusterStatusCode.valueOf( response.getStatus().name() ), response.isTimedOut() );
    }

    @Override
    public void closeIndices( final String... indices )
    {
        for ( final String indexName : indices )
        {
            CloseIndexRequestBuilder closeIndexRequestBuilder = new CloseIndexRequestBuilder( this.client.admin().indices() ).
                setIndices( indexName );

            try
            {
                this.client.admin().indices().close( closeIndexRequestBuilder.request() ).actionGet();
                LOG.info( "Closed index " + indexName );
            }
            catch ( IndexMissingException e )
            {
                LOG.warn( "Could not close index [" + indexName + "], not found" );
            }
        }
    }

    @Override
    public void openIndices( final String... indices )
    {
        for ( final String indexName : indices )
        {
            OpenIndexRequestBuilder openIndexRequestBuilder = new OpenIndexRequestBuilder( this.client.admin().indices() ).
                setIndices( indexName );

            try
            {
                this.client.admin().indices().open( openIndexRequestBuilder.request() ).actionGet();
                LOG.info( "Opened index " + indexName );
            }
            catch ( ElasticsearchException e )
            {
                LOG.error( "Could not open index [" + indexName + "]", e );
                throw new IndexException( "Cannot open index [" + indexName + "]", e );
            }
        }
    }

    private void doDeleteIndex( final String indexName )
    {
        final DeleteIndexRequest req = new DeleteIndexRequest( indexName );

        try
        {
            client.admin().indices().delete( req ).actionGet( DELETE_INDEX_TIMEOUT );
            LOG.info( "Deleted index {}", indexName );
        }
        catch ( ElasticsearchException e )
        {
            LOG.warn( "Failed to delete index {}", indexName );
        }
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
