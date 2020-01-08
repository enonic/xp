package com.enonic.xp.elasticsearch.client.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.Callable;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.repositories.delete.DeleteRepositoryRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesRequest;
import org.elasticsearch.action.admin.cluster.repositories.get.GetRepositoriesResponse;
import org.elasticsearch.action.admin.cluster.repositories.put.PutRepositoryRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.action.admin.cluster.snapshots.delete.DeleteSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsRequest;
import org.elasticsearch.action.admin.cluster.snapshots.get.GetSnapshotsResponse;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotRequest;
import org.elasticsearch.action.admin.cluster.snapshots.restore.RestoreSnapshotResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.client.indices.CloseIndexResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.xp.elasticsearch.client.impl.nodes.GetNodesResponse;

public final class EsClient
    implements Closeable
{
    private final RestHighLevelClient client;

    public EsClient( String hostname )
    {
        this( hostname, 9200 );
    }

    public EsClient( String hostname, int port )
    {
        client = new RestHighLevelClient( RestClient.builder( new HttpHost( hostname, port, "http" ) ) );
    }

    public GetRepositoriesResponse snapshotGetRepository( GetRepositoriesRequest getRepositoriesRequest )
    {
        return wrap( () -> client.snapshot().getRepository( getRepositoriesRequest, RequestOptions.DEFAULT ) );
    }

    public AcknowledgedResponse snapshotCreateRepository( PutRepositoryRequest putRepositoryRequest )
    {
        return wrap( () -> client.snapshot().createRepository( putRepositoryRequest, RequestOptions.DEFAULT ) );
    }

    public AcknowledgedResponse snapshotDeleteRepository( DeleteRepositoryRequest deleteRepositoryRequest )
    {
        return wrap( () -> client.snapshot().deleteRepository( deleteRepositoryRequest, RequestOptions.DEFAULT ) );
    }

    public GetSnapshotsResponse snapshotGet( GetSnapshotsRequest getSnapshotsRequest )
    {
        return wrap( () -> client.snapshot().get( getSnapshotsRequest, RequestOptions.DEFAULT ) );
    }

    public CreateSnapshotResponse snapshotCreate( CreateSnapshotRequest createSnapshotRequest )
    {
        return wrap( () -> client.snapshot().create( createSnapshotRequest, RequestOptions.DEFAULT ) );
    }

    public RestoreSnapshotResponse snapshotRestore( RestoreSnapshotRequest restoreSnapshotRequest )
    {
        return wrap( () -> client.snapshot().restore( restoreSnapshotRequest, RequestOptions.DEFAULT ) );
    }

    public AcknowledgedResponse snapshotDelete( DeleteSnapshotRequest deleteSnapshotRequest )
    {
        return wrap( () -> client.snapshot().delete( deleteSnapshotRequest, RequestOptions.DEFAULT ) );
    }

    public ClusterHealthResponse clusterHealth( ClusterHealthRequest clusterHealthRequest )
    {
        return wrap( () -> client.cluster().health( clusterHealthRequest, RequestOptions.DEFAULT ) );
    }

    public CreateIndexResponse indicesCreate( CreateIndexRequest createIndexRequest )
    {
        return wrap( () -> client.indices().create( createIndexRequest, RequestOptions.DEFAULT ) );
    }

    public AcknowledgedResponse indicesDelete( DeleteIndexRequest deleteIndexRequest )
    {
        return wrap( () -> client.indices().delete( deleteIndexRequest, RequestOptions.DEFAULT ) );
    }

    public AcknowledgedResponse indicesPutSettings( UpdateSettingsRequest updateSettingsRequest )
    {
        return wrap( () -> client.indices().putSettings( updateSettingsRequest, RequestOptions.DEFAULT ) );
    }

    public GetSettingsResponse indicesGetSettings( GetSettingsRequest getSettingsRequest )
    {
        return wrap( () -> client.indices().getSettings( getSettingsRequest, RequestOptions.DEFAULT ) );
    }

    public GetMappingsResponse indicesGetMapping( GetMappingsRequest getMappingsRequest )
    {
        return wrap( () -> client.indices().getMapping( getMappingsRequest, RequestOptions.DEFAULT ) );
    }

    public AcknowledgedResponse indicesPutMapping( PutMappingRequest putMappingRequest )
    {
        return wrap( () -> client.indices().putMapping( putMappingRequest, RequestOptions.DEFAULT ) );
    }

    public RefreshResponse indicesRefresh( final RefreshRequest refreshRequest )
    {
        return wrap( () -> client.indices().refresh( refreshRequest, RequestOptions.DEFAULT ) );
    }

    public CloseIndexResponse indicesClose( CloseIndexRequest closeIndexRequest )
    {
        return wrap( () -> client.indices().close( closeIndexRequest, RequestOptions.DEFAULT ) );
    }

    public OpenIndexResponse indicesOpen( OpenIndexRequest openIndexRequest )
    {
        return wrap( () -> client.indices().open( openIndexRequest, RequestOptions.DEFAULT ) );
    }

    public boolean indicesExists( GetIndexRequest getIndexRequest )
    {
        return wrap( () -> client.indices().exists( getIndexRequest, RequestOptions.DEFAULT ) );
    }

    public GetResponse get( GetRequest getRequest )
    {
        return wrap( () -> client.get( getRequest, RequestOptions.DEFAULT ) );
    }

    public MultiGetResponse mget( MultiGetRequest getRequest )
    {
        return wrap( () -> client.mget( getRequest, RequestOptions.DEFAULT ) );
    }

    public BulkResponse bulk( BulkRequest bulkRequest )
    {
        return wrap( () -> client.bulk( bulkRequest, RequestOptions.DEFAULT ) );
    }

    public SearchResponse scroll( SearchScrollRequest searchScrollRequest )
    {
        return wrap( () -> client.scroll( searchScrollRequest, RequestOptions.DEFAULT ) );
    }

    public CountResponse count( CountRequest countRequest )
    {
        return wrap( () -> client.count( countRequest, RequestOptions.DEFAULT ) );
    }

    public ClearScrollResponse clearScroll( ClearScrollRequest clearScrollRequest )
    {
        return wrap( () -> client.clearScroll( clearScrollRequest, RequestOptions.DEFAULT ) );
    }

    public DeleteResponse delete( DeleteRequest deleteRequest )
    {
        return wrap( () -> client.delete( deleteRequest, RequestOptions.DEFAULT ) );
    }

    public IndexResponse index( IndexRequest indexRequest )
    {
        return wrap( () -> client.index( indexRequest, RequestOptions.DEFAULT ) );
    }

    public SearchResponse search( SearchRequest searchRequest )
    {
        return wrap( () -> client.search( searchRequest, RequestOptions.DEFAULT ) );
    }

    public GetNodesResponse nodes()
    {
        return wrap( () -> {
            final Response response = client.getLowLevelClient().performRequest( new Request( HttpGet.METHOD_NAME, "_nodes" ) );

            return GetNodesResponse.fromResponse( response );
        } );
    }

    public static XContentBuilder jsonBuilder()
    {
        return wrap( XContentFactory::jsonBuilder );
    }

    private static <T> T wrap( final Callable<T> function )
    {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader( RestHighLevelClient.class.getClassLoader() );
        try
        {
            return function.call();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        catch ( Exception e )
        {
            if ( e instanceof RuntimeException )
            {
                throw (RuntimeException) e;
            }
            else
            {
                throw new RuntimeException( e );
            }
        }
        finally
        {
            currentThread.setContextClassLoader( contextClassLoader );
        }
    }

    @Override
    public void close()
        throws IOException
    {
        client.close();
    }
}
