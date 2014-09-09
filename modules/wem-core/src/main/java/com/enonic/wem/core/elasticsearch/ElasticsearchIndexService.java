package com.enonic.wem.core.elasticsearch;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.stats.IndexStats;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.IndexStatus;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.repository.IndexNameResolver;
import com.enonic.wem.core.repository.StorageNameResolver;

@Singleton
public class ElasticsearchIndexService
    implements IndexService
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexService.class );

    private ElasticsearchDao elasticsearchDao;

    private final TimeValue WAIT_FOR_YELLOW_TIMEOUT = TimeValue.timeValueSeconds( 5 );

    public static final TimeValue CLUSTER_NOWAIT_TIMEOUT = TimeValue.timeValueSeconds( 5 );

    private String deleteTimout = "5s";

    private String createTimeout = "5s";

    private String applyMappingTimeout = "5s";

    private Client client;

    @PostConstruct
    public void start()
        throws Exception
    {
    }

    @PreDestroy
    public void stop()
        throws Exception
    {
        this.client.close();
    }

    private IndexStatus getIndexStatus( final Index index, final boolean waitForStatusYellow )
    {
        final ClusterHealthResponse clusterHealth = getClusterHealth( index, waitForStatusYellow );

        LOG.info( "Cluster in state: " + clusterHealth.getStatus().toString() );

        return IndexStatus.valueOf( clusterHealth.getStatus().name() );
    }

    private ClusterHealthResponse getClusterHealth( Index index, boolean waitForYellow )
    {
        ClusterHealthRequest request = new ClusterHealthRequest( index.getName() );

        if ( waitForYellow )
        {
            request.waitForYellowStatus().timeout( WAIT_FOR_YELLOW_TIMEOUT );
        }
        else
        {
            request.timeout( CLUSTER_NOWAIT_TIMEOUT );
        }

        final ClusterHealthResponse clusterHealthResponse = this.client.admin().cluster().health( request ).actionGet();

        if ( clusterHealthResponse.isTimedOut() )
        {
            LOG.warn( "ElasticSearch cluster health timed out" );
        }
        else
        {
            LOG.trace( "ElasticSearch cluster health: Status " + clusterHealthResponse.getStatus().name() + "; " +
                           clusterHealthResponse.getNumberOfNodes() + " nodes; " + clusterHealthResponse.getActiveShards() +
                           " active shards." );
        }

        return clusterHealthResponse;
    }

    private boolean indexExists( final Index index )
    {
        final IndicesExistsResponse exists =
            this.client.admin().indices().exists( new IndicesExistsRequest( index.getName() ) ).actionGet();
        return exists.isExists();
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

    public Set<String> getAllRepositoryIndices( final Repository repository )
    {
        IndicesStatsRequest indicesStatsRequest = new IndicesStatsRequest();
        indicesStatsRequest.listenerThreaded( false );
        indicesStatsRequest.clear();

        final String storageName = StorageNameResolver.resolveStorageIndexName( repository );
        final String searchIndexName = IndexNameResolver.resolveSearchIndexName( repository );

        final IndicesStatsResponse response = this.client.admin().indices().stats( indicesStatsRequest ).actionGet( 10 );

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
    public void deleteIndex( final Collection<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            doDeleteIndex( indexName );
        }
    }

    private void doDeleteIndex( final String indexName )
    {
        final DeleteIndexRequest req = new DeleteIndexRequest( indexName );

        try
        {
            client.admin().indices().delete( req ).actionGet( this.deleteTimout );
            LOG.info( "Deleted index {}", indexName );
        }
        catch ( ElasticsearchException e )
        {
            LOG.warn( "Failed to delete index {}", indexName );
        }
    }

    public void index( final Node node, final Workspace workspace )
    {
        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node, workspace );
        elasticsearchDao.store( indexDocuments );
    }

    public void delete( final EntityId entityId, final Workspace workspace )
    {
        elasticsearchDao.delete( new DeleteDocument( workspace.getSearchIndexName(), IndexType.NODE, entityId.toString() ) );
    }

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }

}
