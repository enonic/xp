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
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.entity.dao.NodeStorageDocument;
import com.enonic.wem.core.entity.dao.NodeStorageDocumentFactory;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.IndexStatus;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument;


public class ElasticsearchIndexServiceImpl
    implements ElasticsearchIndexService
{

    public static final boolean DEFAULT_REFRESH = true;

    private Client client;

    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchIndexServiceImpl.class );

    // TODO: As properties
    private final TimeValue WAIT_FOR_YELLOW_TIMEOUT = TimeValue.timeValueSeconds( 1 );

    public static final TimeValue CLUSTER_NOWAIT_TIMEOUT = TimeValue.timeValueSeconds( 1 );

    private IndexSettingsBuilder indexSettingsBuilder;

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
                type( indexType.getName() ).
                source( xContentBuilder ).
                refresh( indexDocument.doRefreshOnStore() );

            this.client.index( req ).actionGet();
        }
    }

    @Override
    public String storeDocument( final NodeStorageDocument nodeStorageDocument )
    {
        final IndexRequest req = Requests.indexRequest().
            index( nodeStorageDocument.getIndex().getName() ).
            type( nodeStorageDocument.getIndexType().getName() ).
            source( XContentBuilderFactory.create( nodeStorageDocument ) ).
            refresh( DEFAULT_REFRESH );

        if ( nodeStorageDocument.getId() != null )
        {
            req.id( nodeStorageDocument.getId() );
        }

        final IndexResponse indexResponse = this.client.index( req ).actionGet();

        return indexResponse.getId();
    }

    @Override
    public boolean delete( final DeleteDocument deleteDocument )
    {
        DeleteRequest deleteRequest = new DeleteRequest( deleteDocument.getIndexName() ).
            type( deleteDocument.getIndexTypeName() ).
            id( deleteDocument.getId() ).
            refresh( DEFAULT_REFRESH );

        try
        {
            final DeleteResponse deleteResponse = this.client.delete( deleteRequest ).actionGet();
            return deleteResponse.isFound();
        }
        catch ( ElasticsearchException e )
        {
            throw new IndexException( "Failed to delete from index " + deleteDocument.getIndexName() + " of type " +
                                          deleteDocument.getIndexTypeName() + " with id " + deleteDocument.getId(), e );
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
    public SearchResponse search( final ElasticsearchQuery elasticsearchQuery )
    {
        final SearchSourceBuilder searchSource = elasticsearchQuery.toSearchSourceBuilder();

        // System.out.println( searchSource.toString() );

        final SearchRequest searchRequest = Requests.
            searchRequest( elasticsearchQuery.getIndex().getName() ).
            types( elasticsearchQuery.getIndexType().getName() ).
            source( searchSource );

        final SearchResponse searchResponse = doSearchRequest( searchRequest );

        return searchResponse;

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

    @Override
    public SearchResponse get( final ByIdsQuery byIdsQuery )
    {
        final IdsQueryBuilder idsQueryBuilder = new IdsQueryBuilder();

        for ( final IndexDocumentId indexDocumentId : byIdsQuery.getIndexDocumentIds() )
        {
            idsQueryBuilder.addIds( indexDocumentId.getId() );
        }

        return doSearchRequest( byIdsQuery, idsQueryBuilder );
    }

    @Override
    public GetResponse get( final ByIdQuery byIdQuery )
    {
        final GetRequest getRequest = new GetRequest( byIdQuery.index() ).
            type( byIdQuery.indexType() ).
            id( byIdQuery.getId() ).
            fields( NodeStorageDocumentFactory.ENTITY );

        return client.get( getRequest ).actionGet();
    }

    public SearchResponse get( final ByPathQuery byPathQuery )
    {
        final TermQueryBuilder termQueryBuilder = new TermQueryBuilder( NodeStorageDocumentFactory.PATH, byPathQuery.getPath() );

        return doSearchRequest( byPathQuery, termQueryBuilder );
    }

    public SearchResponse get( final ByPathsQuery byPathsQuery )
    {
        final TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder( NodeStorageDocumentFactory.PATH, byPathsQuery.getPaths() );

        return doSearchRequest( byPathsQuery, termsQueryBuilder );
    }

    public SearchResponse get( final ByParentPathQuery byParentPathQuery )
    {
        final TermQueryBuilder pathQuery = new TermQueryBuilder( NodeStorageDocumentFactory.PARENT_PATH, byParentPathQuery.getPath() );

        return doSearchRequest( byParentPathQuery, pathQuery );
    }

    private SearchResponse doSearchRequest( final AbstractByQuery byQuery, final QueryBuilder queryBuilder )
    {
        final SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch( byQuery.index() ).
            setTypes( byQuery.indexType() ).
            setQuery( queryBuilder ).
            setFrom( 0 ).
            setSize( byQuery.size() ).
            addField( NodeStorageDocumentFactory.ENTITY );

        final SearchResponse searchResponse = searchRequestBuilder.
            execute().
            actionGet();

        return searchResponse;
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
