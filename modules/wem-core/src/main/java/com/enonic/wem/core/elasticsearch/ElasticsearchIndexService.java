package com.enonic.wem.core.elasticsearch;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.index.IndexStatus;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.lifecycle.LifecycleBean;
import com.enonic.wem.core.lifecycle.RunLevel;

@Singleton
public class ElasticsearchIndexService
    extends LifecycleBean
    implements IndexService
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexService.class );

    private ElasticsearchDao elasticsearchDao;

    private IndexMappingProvider indexMappingProvider;

    private final TimeValue WAIT_FOR_YELLOW_TIMEOUT = TimeValue.timeValueSeconds( 1 );

    public static final TimeValue CLUSTER_NOWAIT_TIMEOUT = TimeValue.timeValueSeconds( 1 );

    private IndexSettingsBuilder indexSettingsBuilder;

    private Client client;

    public ElasticsearchIndexService()
    {
        super( RunLevel.L3 );
    }

    @Override
    protected void doStart()
        throws Exception
    {
        doInitializeStoreIndex();
        doInitializeNoDbIndex();
        doInitializeWorkspaceIndex();
        doInitializeVersionIndex();
    }

    @Override
    protected void doStop()
        throws Exception
    {
        this.client.close();
    }

    private void doInitializeNoDbIndex()
        throws Exception
    {
        getIndexStatus( Index.NODB, true );

        if ( !indexExists( Index.NODB ) )
        {
            createIndex( Index.NODB );
        }
    }

    private void doInitializeVersionIndex()
        throws Exception
    {
        getIndexStatus( Index.VERSION, true );

        if ( !indexExists( Index.VERSION ) )
        {
            createIndex( Index.VERSION );
        }
    }


    private void doInitializeWorkspaceIndex()
        throws Exception
    {
        getIndexStatus( Index.WORKSPACE, true );

        if ( !indexExists( Index.WORKSPACE ) )
        {
            createIndex( Index.WORKSPACE );
        }
    }

    private void doInitializeStoreIndex()
        throws Exception
    {
        getIndexStatus( Index.STORE, true );

        if ( !indexExists( Index.STORE ) )
        {
            createIndex( Index.STORE );
        }
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

    public void createIndex( Index index )
    {
        LOG.debug( "creating index: " + index.getName() );

        CreateIndexRequest createIndexRequest = new CreateIndexRequest( index.getName() );
        createIndexRequest.settings( indexSettingsBuilder.buildIndexSettings() );

        try
        {
            client.admin().indices().create( createIndexRequest ).actionGet();
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to create index:" + index, e );
        }

        applyMappings( index );

        LOG.info( "Created index: " + index );
    }

    private void applyMappings( final Index index )
    {
        final List<IndexMapping> allIndexMappings = indexMappingProvider.getMappingsForIndex( index );

        for ( IndexMapping indexMapping : allIndexMappings )
        {
            doPutMapping( indexMapping );
        }
    }

    private void doPutMapping( final IndexMapping indexMapping )
    {
        final Index index = indexMapping.getIndex();
        final String indexType = indexMapping.getIndexType();
        final String source = indexMapping.getSource();

        Preconditions.checkNotNull( index );
        Preconditions.checkNotNull( indexType );
        Preconditions.checkNotNull( source );

        PutMappingRequest mappingRequest = new PutMappingRequest( index.getName() ).type( indexType ).source( source );

        try
        {
            this.client.admin().indices().putMapping( mappingRequest ).actionGet();
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to apply mapping to index: " + index, e );
        }

        LOG.info( "Mapping for index " + index + ", index-type: " + indexType + " deleted" );
    }

    public void deleteIndex( final Index... indexes )
    {
        for ( final Index index : indexes )
        {
            doDeleteIndex( index );
        }
    }

    private void doDeleteIndex( final Index index )
    {
        final DeleteIndexRequest req = new DeleteIndexRequest( index.getName() );

        try
        {
            client.admin().indices().delete( req ).actionGet();
        }
        catch ( ElasticsearchException e )
        {
            LOG.warn( "Failed to delte index " + index.getName() );
        }
    }

    public void index( final Node node )
    {
        final Collection<IndexDocument> indexDocuments = NodeIndexDocumentFactory.create( node );
        elasticsearchDao.store( indexDocuments );
    }

    public void delete( final EntityId entityId )
    {
        elasticsearchDao.delete( new DeleteDocument( Index.NODB, IndexType.NODE, entityId.toString() ) );
    }

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

    @Inject
    public void setIndexMappingProvider( final IndexMappingProvider indexMappingProvider )
    {
        this.indexMappingProvider = indexMappingProvider;
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }

    @Inject
    public void setIndexSettingsBuilder( final IndexSettingsBuilder indexSettingsBuilder )
    {
        this.indexSettingsBuilder = indexSettingsBuilder;
    }

    @Override
    public long countDocuments( final Index index )
    {
        final CountRequest request = new CountRequest().indices( index.getName() );
        final CountResponse response = this.client.count( request ).actionGet();
        return response.getCount();
    }
}
