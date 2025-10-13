package com.enonic.xp.repo.impl.elasticsearch;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.state.ClusterStateAction;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.close.CloseIndexAction;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsAction;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexAction;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexNotFoundException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexMapping;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.index.IndexSettings;
import com.enonic.xp.repo.impl.index.UpdateIndexSettings;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryId;


@Component
public class IndexServiceInternalImpl
    implements IndexServiceInternal
{
    private static final Logger LOG = LoggerFactory.getLogger( IndexServiceInternalImpl.class );

    private static final String ES_DEFAULT_INDEX_TYPE_NAME = "_default_";

    private static final String DELETE_INDEX_TIMEOUT = "5s";

    private static final String CREATE_INDEX_TIMEOUT = "5s";

    private static final String UPDATE_INDEX_TIMEOUT = "5s";

    private static final String INDEX_EXISTS_TIMEOUT = "5s";

    private static final String CLUSTER_STATE_TIMEOUT = "5s";

    private static final String GET_SETTINGS_TIMEOUT = "5s";

    private static final String CLUSTER_HEALTH_TIMEOUT = "10s";

    private final Client client;

    @Activate
    public IndexServiceInternalImpl( @Reference final Client client )
    {
        this.client = client;
    }

    @Override
    public void refresh( final String... indexNames )
    {
        client.admin().indices().prepareRefresh( indexNames ).execute().actionGet();
    }

    @Override
    public boolean isMaster()
    {
        final ClusterStateRequestBuilder requestBuilder =
            new ClusterStateRequestBuilder( this.client.admin().cluster(), ClusterStateAction.INSTANCE ).setBlocks( false )
                .setIndices()
                .setBlocks( false )
                .setMetaData( false )
                .setNodes( true )
                .setRoutingTable( false );

        final ClusterStateResponse clusterStateResponse =
            client.admin().cluster().state( requestBuilder.request() ).actionGet( CLUSTER_STATE_TIMEOUT );

        return clusterStateResponse.getState().nodes().localNodeMaster();
    }

    @Override
    public void createIndex( final com.enonic.xp.repo.impl.index.CreateIndexRequest request )
    {
        final String indexName = request.getIndexName();
        final IndexSettings indexSettings = request.getIndexSettings();
        LOG.info( "creating index {}", indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( indexSettings.getData() );
        if ( request.getMappings() != null )
        {
            for ( Map.Entry<IndexType, IndexMapping> mappingEntry : request.getMappings().entrySet() )
            {
                createIndexRequest.mapping(
                    mappingEntry.getKey().isDynamicTypes() ? ES_DEFAULT_INDEX_TYPE_NAME : mappingEntry.getKey().getName(),
                    mappingEntry.getValue().getData() );
            }
        }

        try
        {
            final CreateIndexResponse createIndexResponse =
                client.admin().indices().create( createIndexRequest ).actionGet( CREATE_INDEX_TIMEOUT );

            LOG.info( "Index {} created with status {}", indexName, createIndexResponse.isAcknowledged() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to create index: " + indexName, e );
        }
    }


    public void putIndexMapping( RepositoryId repositoryId, IndexType indexType, Map<String, Object> mapping ) {
        final String indexName = IndexType.SEARCH == indexType
            ? IndexNameResolver.resolveSearchIndexName( repositoryId )
            : IndexNameResolver.resolveStorageIndexName( repositoryId );
        LOG.info( "updating index mapping {}", indexName );
        try
        {
            client.admin().indices().putMapping( Requests.putMappingRequest( indexName).type(indexType.getName()).source( mapping ) ).actionGet(UPDATE_INDEX_TIMEOUT);
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to update index mapping: " + indexName, e );
        }

    }
    @Override
    public void updateIndex( final String indexName, final UpdateIndexSettings settings )
    {
        LOG.info( "updating index {}", indexName );

        final UpdateSettingsRequest updateSettingsRequest =
            new UpdateSettingsRequest().indices( indexName ).settings( settings.getSettingsAsString() );
        try
        {
            final UpdateSettingsResponse updateSettingsResponse =
                client.admin().indices().updateSettings( updateSettingsRequest ).actionGet( UPDATE_INDEX_TIMEOUT );

            LOG.info( "Index {} updated with status {}", indexName, updateSettingsResponse.isAcknowledged() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to update index: " + indexName, e );
        }
    }

    @Override
    public Map<String, String> getIndexSettings( final RepositoryId repositoryId, final IndexType indexType )
    {
        if ( repositoryId == null || indexType == null )
        {
            return null;
        }

        final String indexName = IndexType.SEARCH == indexType
            ? IndexNameResolver.resolveSearchIndexName( repositoryId )
            : IndexNameResolver.resolveStorageIndexName( repositoryId );

        final ImmutableOpenMap<String, Settings> settingsMap = this.client.admin()
            .indices()
            .getSettings( new GetSettingsRequest().indices( indexName ) )
            .actionGet( GET_SETTINGS_TIMEOUT )
            .getIndexToSettings();

        return settingsMap.get( indexName ).getAsMap();
    }

    @Override
    public Map<String, Object> getIndexMapping( final RepositoryId repositoryId, final IndexType indexType )
    {
        final String indexName = IndexType.SEARCH == indexType
            ? IndexNameResolver.resolveSearchIndexName( repositoryId )
            : IndexNameResolver.resolveStorageIndexName( repositoryId );

        final ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> repoMappings = this.client.admin()
            .indices()
            .getMappings( new GetMappingsRequest().indices( indexName ) )
            .actionGet( GET_SETTINGS_TIMEOUT )
            .getMappings();

        final ImmutableOpenMap<String, MappingMetaData> indexTypeMappings = repoMappings.get( indexName );

        final MappingMetaData mappingMetaData = indexTypeMappings.get( IndexType.SEARCH == indexType ? indexName : indexType.getName() );

        try
        {
            return mappingMetaData.getSourceAsMap();
        }
        catch ( IOException e )
        {
            throw new IndexException( "Failed to get index mapping of index: " + indexName, e );
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
        IndicesExistsRequest request =
            new IndicesExistsRequestBuilder( this.client.admin().indices(), IndicesExistsAction.INSTANCE ).setIndices( indices ).request();

        final IndicesExistsResponse response = client.admin().indices().exists( request ).actionGet( INDEX_EXISTS_TIMEOUT );

        return response.isExists();
    }

    @Override
    public boolean waitForYellowStatus( final String... indexNames )
    {
        ClusterHealthRequest request = new ClusterHealthRequest( indexNames );

        request.waitForYellowStatus().timeout( CLUSTER_HEALTH_TIMEOUT );

        final ClusterHealthResponse response;
        try
        {
            final Stopwatch timer = Stopwatch.createStarted();
            response = this.client.admin().cluster().health( request ).actionGet();
            timer.stop();
            LOG.debug( "ElasticSearch cluster '{}' " +
                           "health (timedOut={}, timeOutValue={}, used={}): Status={}, nodes={}, active shards={}, indices={}",
                       response.getClusterName(), response.isTimedOut(), CLUSTER_HEALTH_TIMEOUT, timer, response.getStatus(),
                       response.getNumberOfNodes(), response.getActiveShards(), response.getIndices() );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to get cluster health status", e );
            return false;
        }

        if ( response.isTimedOut() || response.getStatus() == ClusterHealthStatus.RED )
        {
            LOG.error( "Cluster not healthy: timed out: {}, state: {}", response.isTimedOut(), response.getStatus() );
            return false;
        }

        return true;
    }

    @Override
    public void closeIndices( final String... indices )
    {
        for ( final String indexName : indices )
        {
            CloseIndexRequestBuilder closeIndexRequestBuilder =
                new CloseIndexRequestBuilder( this.client.admin().indices(), CloseIndexAction.INSTANCE ).setIndices( indexName );

            try
            {
                this.client.admin().indices().close( closeIndexRequestBuilder.request() ).actionGet();
                LOG.info( "Closed index " + indexName );
            }
            catch ( IndexNotFoundException e )
            {
                LOG.warn( "Could not close index [{}], not found", indexName );
            }
        }
    }

    @Override
    public void openIndices( final String... indices )
    {
        for ( final String indexName : indices )
        {
            OpenIndexRequestBuilder openIndexRequestBuilder =
                new OpenIndexRequestBuilder( this.client.admin().indices(), OpenIndexAction.INSTANCE ).setIndices( indexName );

            try
            {
                this.client.admin().indices().open( openIndexRequestBuilder.request() ).actionGet();
                LOG.info( "Opened index {}", indexName );
            }
            catch ( ElasticsearchException e )
            {
                LOG.error( "Could not open index [{}]", indexName, e );
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
            LOG.warn( "Failed to delete index {}", indexName, e );
        }
    }
}
