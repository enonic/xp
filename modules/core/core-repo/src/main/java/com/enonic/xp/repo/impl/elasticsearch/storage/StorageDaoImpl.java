package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetAction;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeStorageException;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.StorageName;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.executor.CopyExecutor;
import com.enonic.xp.repo.impl.elasticsearch.result.GetResultFactory;
import com.enonic.xp.repo.impl.elasticsearch.xcontent.StoreDocumentXContentBuilderFactory;
import com.enonic.xp.repo.impl.storage.CopyRequest;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByIdsRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.RoutableId;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repository.IndexException;

@Component
public class StorageDaoImpl
    implements StorageDao
{
    private static final long DEFAULT_STORE_TIMEOUT_SECONDS = 10;

    private Client client;


    @Activate
    public StorageDaoImpl( @Reference final Client client )
    {
        this.client = client;
    }

    @Override
    public String store( final StoreRequest request )
    {
        final StorageSource settings = request.getSettings();

        final IndexRequest indexRequest = Requests.indexRequest().id( request.getId() ).index( settings.getStorageName().getName() ).
            type( settings.getStorageType().getName() ).
            source( XContentBuilderFactory.create( request ) ).
            refresh( request.isForceRefresh() );

        if ( request.getRouting() != null )
        {
            indexRequest.routing( request.getRouting() );
        }

        if ( request.getParent() != null )
        {
            indexRequest.parent( request.getParent() );
        }

        final int timeout = request.getTimeout();
        try
        {
            return this.client.index( indexRequest ).actionGet( timeout, TimeUnit.SECONDS ).getId();
        }
        catch ( ClusterBlockException e )
        {
            throw new NodeStorageException( "Cannot store node " + indexRequest.id() + ", Repository in 'READ-ONLY mode'" );
        }
        catch ( ElasticsearchException e )
        {
            throw new NodeStorageException( "Cannot store node " + indexRequest.toString(), e );
        }
    }

    @Override
    public void store( final IndexDocument indexDocument )
    {
        final String id = indexDocument.getId();

        final XContentBuilder xContentBuilder = StoreDocumentXContentBuilderFactory.create( indexDocument );

        final IndexRequest req = Requests.indexRequest()
            .id( id )
            .index( indexDocument.getIndexName() )
            .type( indexDocument.getIndexTypeName() )
            .source( xContentBuilder )
            .refresh( indexDocument.isRefreshAfterOperation() );

        try
        {
            this.client.index( req ).actionGet( DEFAULT_STORE_TIMEOUT_SECONDS, TimeUnit.SECONDS );
        }
        catch ( Exception e )
        {
            final String msg = "Failed to store document with id [" + id + "] in index [" + indexDocument.getIndexName() + "] branch " +
                indexDocument.getIndexTypeName();

            throw new IndexException( msg, e );
        }
    }

    @Override
    public void delete( final DeleteRequests requests )
    {
        final StorageSource settings = requests.getSettings();

        for ( final RoutableId id : requests.getIds() )
        {
            try
            {
                final org.elasticsearch.action.delete.DeleteRequest request =
                    new DeleteRequestBuilder( this.client, DeleteAction.INSTANCE ).
                        setIndex( settings.getStorageName().getName() ).
                        setType( settings.getStorageType().getName() ).
                        setRefresh( requests.isForceRefresh() ).
                        setId( id.id ).
                        setRouting( id.routing ).
                        request();

                this.client.delete( request ).actionGet( requests.getTimeout(), TimeUnit.SECONDS );
            }
            catch ( ClusterBlockException e )
            {
                throw new NodeStorageException( "Cannot delete node " + id + ", Repository in 'READ-ONLY mode'" );
            }
            catch ( Exception e )
            {
                throw new NodeStorageException( "Cannot delete node " + id, e );
            }
        }
    }

    @Override
    public GetResult getById( final GetByIdRequest request )
    {
        final StorageSource storageSource = request.getStorageSource();
        final GetRequest getRequest = new GetRequest( storageSource.getStorageName().getName() ).
            type( storageSource.getStorageType().getName() ).
            preference( Objects.requireNonNullElse( request.getSearchPreference(), SearchPreference.LOCAL ).getName() ).
            id( request.getId() );

        if ( request.getReturnFields().isNotEmpty() )
        {
            getRequest.fields( request.getReturnFields().getReturnFieldNames() );
        }

        if ( request.getRouting() != null )
        {
            getRequest.routing( request.getRouting() );
        }

        final GetResponse getResponse = client.get( getRequest ).
            actionGet( request.getTimeout(), TimeUnit.SECONDS );

        return GetResultFactory.create( getResponse );
    }

    @Override
    public List<GetResult> getByIds( final GetByIdsRequest requests )
    {
        if ( requests.getRequests().isEmpty() )
        {
            return List.of();
        }

        final MultiGetRequestBuilder multiGetRequestBuilder =
            new MultiGetRequestBuilder( this.client, MultiGetAction.INSTANCE ).setPreference(
                Objects.requireNonNullElse( requests.getSearchPreference(), SearchPreference.LOCAL ).getName() );

        for ( final GetByIdRequest request : requests.getRequests() )
        {
            final StorageSource storageSource = request.getStorageSource();

            final MultiGetRequest.Item item =
                new MultiGetRequest.Item( storageSource.getStorageName().getName(), storageSource.getStorageType().getName(),
                                          request.getId() );

            if ( request.getReturnFields().isNotEmpty() )
            {
                item.fields( request.getReturnFields().getReturnFieldNames() );
            }

            if ( request.getRouting() != null )
            {
                item.routing( request.getRouting() );
            }

            multiGetRequestBuilder.add( item );
        }

        final MultiGetResponse multiGetItemResponses = this.client.multiGet( multiGetRequestBuilder.request() ).actionGet();

        return Stream.of( multiGetItemResponses.getResponses() )
            .map( MultiGetItemResponse::getResponse )
            .map( GetResultFactory::create )
            .collect( Collectors.toUnmodifiableList() );
    }

    @Override
    public void copy( final CopyRequest request )
    {
        if ( request.getNodeIds().isEmpty() )
        {
            return;
        }

        CopyExecutor.create( this.client ).request( request ).build().execute();
    }

    @Override
    public void refresh( final StorageName storageName )
    {
        client.admin().indices().prepareRefresh( storageName.getName() ).execute().actionGet();
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
