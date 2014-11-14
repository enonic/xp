package com.enonic.wem.core.elasticsearch;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.core.elasticsearch.document.DeleteDocument;
import com.enonic.wem.core.elasticsearch.document.StoreDocument;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.repository.IndexNameResolver;
import com.enonic.wem.core.repository.StorageNameResolver;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;
import com.enonic.wem.repo.NodeVersionId;

public class ElasticsearchIndexService
    implements IndexService
{

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexService.class );

    private static final String INDICES_RESPONSE_TIMEOUT = "10s";

    private ElasticsearchDao elasticsearchDao;

    private final static String deleteTimeout = "5s";

    private final static String createTimeout = "5s";

    private final static String applyMappingTimeout = "5s";

    private final static String existsTimeout = "5s";

    private Client client;

    public ClusterHealthStatus getClusterHealth( final TimeValue timeout, final String... indexNames )
    {
        return doGetClusterHealth( timeout, indexNames );
    }

    @Override
    public void refresh( final String... indexNames )
    {
        this.client.admin().indices().refresh(
            new RefreshRequestBuilder( this.client.admin().indices() ).setIndices( indexNames ).request() ).
            actionGet();
    }

    private ClusterHealthStatus doGetClusterHealth( final TimeValue timeout, final String... indexNames )
    {
        LOG.info( "Executing ClusterHealtRequest" );

        ClusterHealthRequest request = indexNames != null ? new ClusterHealthRequest( indexNames ) : new ClusterHealthRequest();

        request.waitForYellowStatus().timeout( timeout );

        final ClusterHealthResponse response = this.client.admin().cluster().health( request ).actionGet();

        LOG.info( "ElasticSearch cluster health (timedOut={}, timeOutValue={}): Status={}, nodes={}, active shards={}, indices={}",
                  new Object[]{response.isTimedOut(), timeout, response.getStatus(), response.getNumberOfNodes(),
                      response.getActiveShards(), response.getIndices().keySet()} );

        return new ClusterHealthStatus( ClusterStatusCode.valueOf( response.getStatus().name() ), response.isTimedOut() );
    }

    public void createIndex( final String indexName, final String settings )
    {
        LOG.info( "creating index {}", indexName );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( indexName );
        createIndexRequest.settings( settings );

        try
        {
            final CreateIndexResponse createIndexResponse =
                client.admin().indices().create( createIndexRequest ).actionGet( this.createTimeout );

            LOG.info( "Index {} created with status {}", indexName, createIndexResponse.isAcknowledged() );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to create index: " + indexName, e );
        }
    }

    public void applyMapping( final String indexName, final String indexType, final String mapping )
    {
        LOG.info( "Apply mapping for index {}", indexName );

        PutMappingRequest mappingRequest = new PutMappingRequest( indexName ).type( indexType ).source( mapping );

        try
        {
            this.client.admin().indices().putMapping( mappingRequest ).actionGet( this.applyMappingTimeout );
            LOG.info( "Mapping for index {} applied", indexName );
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to apply mapping to index: " + indexName, e );
        }
    }

    public Set<String> getAllRepositoryIndices( final RepositoryId repositoryId )
    {
        IndicesStatsRequest indicesStatsRequest = new IndicesStatsRequest();
        indicesStatsRequest.listenerThreaded( false );
        indicesStatsRequest.clear();

        final String storageName = StorageNameResolver.resolveStorageIndexName( repositoryId );
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repositoryId );

        final IndicesStatsResponse response =
            this.client.admin().indices().stats( indicesStatsRequest ).actionGet( INDICES_RESPONSE_TIMEOUT );

        final Map<String, IndexStats> indicesMap = response.getIndices();

        final Set<String> indexNames = Sets.newHashSet();

        // TODO as filter
        for ( final String indexName : indicesMap.keySet() )
        {
            if ( indexName.startsWith( storageName ) || ( indexName.startsWith( searchIndexName ) ) )
            {
                indexNames.add( indexName );
            }
        }

        return indexNames;
    }

    @Override
    public void deleteIndices( final Collection<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            doDeleteIndex( indexName );
        }
    }

    public boolean indicesExists( final String... indices )
    {
        IndicesExistsRequest request = new IndicesExistsRequestBuilder( this.client.admin().indices() ).setIndices( indices ).request();

        final IndicesExistsResponse response = client.admin().indices().exists( request ).actionGet( existsTimeout );

        return response.isExists();
    }

    private void doDeleteIndex( final String indexName )
    {
        final DeleteIndexRequest req = new DeleteIndexRequest( indexName );

        try
        {
            client.admin().indices().delete( req ).actionGet( this.deleteTimeout );
            LOG.info( "Deleted index {}", indexName );
        }
        catch ( ElasticsearchException e )
        {
            LOG.warn( "Failed to delete index {}", indexName );
        }
    }

    public void store( final Node node, final NodeVersionId nodeVersionId, final IndexContext context )
    {
        final Collection<StoreDocument> storeDocuments =
            NodeStoreDocumentFactory.create( node, nodeVersionId, context.getWorkspace(), context.getRepositoryId() );

        elasticsearchDao.store( storeDocuments );
    }

    public void delete( final NodeId nodeId, final IndexContext context )
    {
        final String indexName = IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() );
        final String indexType = context.getWorkspace().getName();

        elasticsearchDao.delete( DeleteDocument.create().
            indexName( indexName ).
            indexTypeName( indexType ).
            id( nodeId.toString() ).
            build() );
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

    public void setClient( final Client client )
    {
        this.client = client;
    }
}
