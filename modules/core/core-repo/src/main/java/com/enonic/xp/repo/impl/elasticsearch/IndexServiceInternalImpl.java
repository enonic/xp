package com.enonic.xp.repo.impl.elasticsearch;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.index.ApplyMappingRequest;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.index.UpdateIndexSettings;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.IndexMapping;
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

    private EsClient client;

    @Override
    public ClusterHealthStatus getClusterHealth( final String timeout, final String... indexNames )
    {
        return doGetClusterHealth( timeout, indexNames );
    }

    @Override
    public void refresh( final String... indexNames )
    {
        client.indicesRefresh( new RefreshRequest( indexNames ) );
    }

    @Override
    public boolean isMaster()
    {
//        final ClusterStateRequest request = new ClusterStateRequest().
//            blocks( false ).
//            metaData( false ).
//            nodes( true ).
//            routingTable( false );
//
//
//
//        final ClusterStateRequestBuilder requestBuilder =
//            new ClusterStateRequestBuilder( this.client.cluster(), ClusterStateAction.INSTANCE ).
//                setBlocks( false ).
//                setIndices().
//                setBlocks( false ).
//                setMetaData( false ).
//                setNodes( true ).
//                setRoutingTable( false );
//
//        final ClusterStateResponse clusterStateResponse =
//            client.admin().cluster().state( requestBuilder.request() ).actionGet( CLUSTER_STATE_TIMEOUT );
//
//            return clusterStateResponse.getState().nodes().isLocalNodeElectedMaster();
        return true; // TODO ES
    }

    @Override
    public void copy( final NodeId nodeId, final RepositoryId repositoryId, final Branch source, final Branch target )
    {
        final GetRequest request = new GetRequest().
            id( nodeId.toString() ).
            index( IndexNameResolver.resolveSearchIndexName( repositoryId, source ) );

        final GetResponse response = this.client.get( request );

        if ( !response.isExists() )
        {
            throw new IndexException( "Could not copy entry with id [" + nodeId + "], does not exist" );
        }

        final Map<String, Object> sourceValues = response.getSource();

        final IndexRequest req = Requests.indexRequest().
            id( nodeId.toString() ).
            index( IndexNameResolver.resolveSearchIndexName( repositoryId, target ) ).
            source( sourceValues ).
            setRefreshPolicy( WriteRequest.RefreshPolicy.NONE );

        this.client.index( req );
    }

    @Override
    public void createIndex( final com.enonic.xp.repo.impl.index.CreateIndexRequest request )
    {
        final String indexName = request.getIndexName();
        final IndexSettings indexSettings = request.getIndexSettings();
        final IndexMapping indexMapping = request.getMapping();
        LOG.info( "creating index {}", indexName );

        final CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( indexSettings.getAsString(), XContentType.JSON );
        createIndexRequest.mapping( indexMapping.getAsString(), XContentType.JSON );
        try
        {
            final CreateIndexResponse createIndexResponse = client.indicesCreate( createIndexRequest );

            LOG.info( "Index {} created with status {}", indexName, createIndexResponse.isAcknowledged() );
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
            settings( settings.getSettingsAsString(), XContentType.JSON ).
            timeout( UPDATE_INDEX_TIMEOUT );

        try
        {
            final AcknowledgedResponse updateSettingsResponse = client.
                indicesPutSettings( updateSettingsRequest );

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
            ? IndexNameResolver.resolveSearchIndexName( repositoryId, ContextAccessor.current().getBranch() )
            : IndexNameResolver.resolveStorageIndexName( repositoryId, indexType );

        if ( indexName == null )
        {
            return null;
        }

        final GetSettingsRequest request = new GetSettingsRequest().indices( indexName );
        request.masterNodeTimeout( GET_SETTINGS_TIMEOUT );

        final GetSettingsResponse response = client.indicesGetSettings( request );
        final ImmutableOpenMap<String, Settings> settingsMap = response.getIndexToSettings();

        final Settings settings = settingsMap.get( indexName );

        final Map<String, Object> settingsAsMap = new HashMap<>();
        settings.keySet().stream().
            filter( settings::hasValue ).
            forEach( key -> settingsAsMap.put( key, settings.get( key ) ) );

        return IndexSettings.from( settingsAsMap );
    }

    @Override
    public Map<String, Object> getIndexMapping( final RepositoryId repositoryId, final Branch branch, final IndexType indexType )
    {

        if ( repositoryId == null || indexType == null )
        {
            return null;
        }

        final String indexName = IndexType.SEARCH == indexType
            ? IndexNameResolver.resolveSearchIndexName( repositoryId, branch )
            : IndexNameResolver.resolveStorageIndexName( repositoryId, indexType );

        if ( indexName == null )
        {
            return null;
        }

        final GetMappingsRequest request = new GetMappingsRequest();
        request.indices( indexName );
        request.setTimeout( TimeValue.timeValueSeconds( 5 ) );

        final GetMappingsResponse response = client.indicesGetMapping( request );

        final MappingMetaData mappingMetaData = response.mappings().get( branch.getValue() );

        return mappingMetaData.getSourceAsMap();
    }

    @Override
    public void applyMapping( final ApplyMappingRequest request )
    {
        final String indexName = request.getIndexName();
        LOG.info( "Apply mapping for index {}", indexName );

        final PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).
            source( request.getMapping().getAsString(), XContentType.JSON );
        mappingRequest.setTimeout( TimeValue.timeValueSeconds( 5 ) );

        try
        {
            client.indicesPutMapping( mappingRequest );

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
        final GetIndexRequest request = new GetIndexRequest( indices );
        request.setTimeout( TimeValue.timeValueSeconds( 5 ) );

        return client.indicesExists( request );
    }

    private ClusterHealthStatus doGetClusterHealth( final String timeout, final String... indexNames )
    {
        ClusterHealthRequest request = indexNames != null ? new ClusterHealthRequest( indexNames ) : new ClusterHealthRequest();

        request.waitForYellowStatus().timeout( timeout );

        final Stopwatch timer = Stopwatch.createStarted();

        final ClusterHealthResponse response = client.clusterHealth( request );
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
            try
            {
                final CloseIndexRequest closeIndexRequest = new CloseIndexRequest( indexName );

                client.indicesClose( closeIndexRequest );

                LOG.info( "Closed index " + indexName );
            }
            catch ( IndexNotFoundException e )
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
            try
            {
                final OpenIndexRequest openIndexRequest = new OpenIndexRequest( indexName );

                client.indicesOpen( openIndexRequest );

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
        final DeleteIndexRequest req = new DeleteIndexRequest( indexName ).
            timeout( DELETE_INDEX_TIMEOUT );

        try
        {
            client.indicesDelete( req );

            LOG.info( "Deleted index {}", indexName );
        }
        catch ( ElasticsearchException e )
        {
            LOG.warn( "Failed to delete index {}", indexName );
        }
    }

    @Reference
    public void setClient( final EsClient client )
    {
        this.client = client;
    }
}
