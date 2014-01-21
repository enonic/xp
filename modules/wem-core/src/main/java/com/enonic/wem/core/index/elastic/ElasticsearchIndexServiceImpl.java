package com.enonic.wem.core.index.elastic;

import java.util.Collection;

import javax.inject.Inject;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.IndexStatus;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.entity.EntityQueryResult;
import com.enonic.wem.core.index.entity.EntityQueryResultFactory;


public class ElasticsearchIndexServiceImpl
    implements ElasticsearchIndexService
{
    private Client client;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexServiceImpl.class );

    // TODO: As properties
    private final TimeValue WAIT_FOR_YELLOW_TIMEOUT = TimeValue.timeValueSeconds( 10 );

    public static final TimeValue CLUSTER_NOWAIT_TIMEOUT = TimeValue.timeValueSeconds( 1 );

    private IndexSettingsBuilder indexSettingsBuilder;

    private EntityQueryResultFactory entityQueryResultFactory = new EntityQueryResultFactory();

    @Override
    public IndexStatus getIndexStatus( final Index index, final boolean waitForStatusYellow )
    {
        final ClusterHealthResponse clusterHealth = getClusterHealth( index, waitForStatusYellow );

        LOG.info( "Cluster in state: " + clusterHealth.getStatus().toString() );

        return IndexStatus.valueOf( clusterHealth.getStatus().name() );
    }

    @Override
    public boolean indexExists( Index index )
    {
        final IndicesExistsResponse exists =
            this.client.admin().indices().exists( new IndicesExistsRequest( index.getName() ) ).actionGet();
        return exists.isExists();
    }

    @Override
    public void indexDocuments( Collection<IndexDocument> indexDocuments )
    {
        for ( IndexDocument indexDocument : indexDocuments )
        {
            final String id = indexDocument.getId();
            final IndexType indexType = indexDocument.getIndexType();
            final Index index = indexDocument.getIndex();

            final XContentBuilder xContentBuilder = XContentBuilderFactory.create( indexDocument );

            final IndexRequest req = Requests.indexRequest().
                id( id ).
                index( index.getName() ).
                type( indexType.getIndexTypeName() ).
                source( xContentBuilder ).
                refresh( indexDocument.doRefreshOnStore() );

            this.client.index( req ).actionGet();
        }
    }


    @Override
    public void delete( final DeleteDocument deleteDocument )
    {
        DeleteRequest deleteRequest =
            new DeleteRequest( deleteDocument.getIndex().getName(), deleteDocument.getIndexType().getIndexTypeName(),
                               deleteDocument.getId() );

        try
        {
            this.client.delete( deleteRequest ).actionGet();
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to delete from index " + deleteDocument.getIndex() + " of type " +
                                          deleteDocument.getIndexType().getIndexTypeName() + " with id " + deleteDocument.getId(), e );
        }
    }

    @Override
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

        LOG.info( "Created index: " + index );
    }

    @Override
    public void putMapping( final IndexMapping indexMapping )
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

    @Override
    public EntityQueryResult search( final ElasticsearchQuery elasticsearchQuery )
    {
        final SearchSourceBuilder searchSource = elasticsearchQuery.toSearchSourceBuilder();

        System.out.println( searchSource.toString() );

        final SearchRequest searchRequest = Requests.
            searchRequest( elasticsearchQuery.getIndex().getName() ).
            types( elasticsearchQuery.getIndexType().getIndexTypeName() ).
            source( searchSource );

        final SearchResponse searchResponse = doSearchRequest( searchRequest );

        return entityQueryResultFactory.create( searchResponse );
    }

    private SearchResponse doSearchRequest( SearchRequest searchRequest )
    {
        return this.client.search( searchRequest ).actionGet();
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

    public void deleteIndex( final Index index )
    {
        final DeleteIndexRequest req = new DeleteIndexRequest( index.getName() );

        try
        {
            client.admin().indices().delete( req ).actionGet();
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to delete index:" + index.getName(), e );
        }
    }
}
