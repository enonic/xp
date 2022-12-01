package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetAction;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeStorageException;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.executor.CopyExecutor;
import com.enonic.xp.repo.impl.elasticsearch.executor.StoreExecutor;
import com.enonic.xp.repo.impl.elasticsearch.result.GetResultFactory;
import com.enonic.xp.repo.impl.storage.CopyRequest;
import com.enonic.xp.repo.impl.storage.DeleteRequest;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByIdsRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;

@Component
public class StorageDaoImpl
    implements StorageDao
{
    private Client client;

    @Override
    public String store( final StoreRequest request )
    {
        final StorageSource settings = request.getSettings();

        final IndexRequest indexRequest = Requests.indexRequest().
            index( settings.getStorageName().getName() ).
            type( settings.getStorageType().getName() ).
            source( XContentBuilderFactory.create( request ) ).
            id( request.getId() ).
            refresh( request.isForceRefresh() );

        if ( request.getRouting() != null )
        {
            indexRequest.routing( request.getRouting() );
        }

        if ( request.getParent() != null )
        {
            indexRequest.parent( request.getParent() );
        }

        return doStore( indexRequest, request.getTimeout() );
    }

    @Override
    public void store( final Collection<IndexDocument> indexDocuments )
    {
        StoreExecutor.create( this.client ).
            build().
            execute( indexDocuments );
    }

    @Override
    public boolean delete( final DeleteRequest request )
    {
        final StorageSource settings = request.getSettings();
        final String id = request.getId();

        final DeleteRequestBuilder builder = new DeleteRequestBuilder( this.client, DeleteAction.INSTANCE ).
            setId( id ).
            setIndex( settings.getStorageName().getName() ).
            setType( settings.getStorageType().getName() ).
            setRefresh( request.isForceRefresh() );

        final DeleteResponse deleteResponse;
        try
        {
            deleteResponse = this.client.delete( builder.request() ).
                actionGet( request.getTimeout(), TimeUnit.SECONDS );
        }
        catch ( ClusterBlockException e )
        {
            throw new NodeStorageException( "Cannot delete node " + id + ", Repository in 'READ-ONLY mode'" );
        }
        catch ( Exception e )
        {
            throw new NodeStorageException( "Cannot delete node " + id, e );
        }

        return deleteResponse.isFound();
    }

    @Override
    public void delete( final DeleteRequests requests )
    {
        final StorageSource settings = requests.getSettings();

        for ( final String id : requests.getIds() )
        {
            try
            {
                final org.elasticsearch.action.delete.DeleteRequest request =
                    new DeleteRequestBuilder( this.client, DeleteAction.INSTANCE ).
                        setIndex( settings.getStorageName().getName() ).
                        setType( settings.getStorageType().getName() ).
                        setRefresh( requests.isForceRefresh() ).
                        setId( id ).
                        setRouting( id ). //TODO Java10
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

    private String doStore( final IndexRequest request, final int timeout )
    {
        final IndexResponse indexResponse;
        try
        {
            indexResponse = this.client.index( request ).
                actionGet( timeout, TimeUnit.SECONDS );
        }
        catch ( ClusterBlockException e )
        {
            throw new NodeStorageException( "Cannot store node " + request.id() + ", Repository in 'READ-ONLY mode'" );
        }
        catch ( ElasticsearchException e )
        {
            throw new NodeStorageException( "Cannot store node " + request.toString(), e );
        }

        return indexResponse.getId();
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

        CopyExecutor.create( this.client ).
            request( request ).
            build().
            execute();
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
