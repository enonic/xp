package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.util.Collection;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.rest.RestStatus;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeStorageException;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.elasticsearch.document.IndexDocument;
import com.enonic.xp.repo.impl.elasticsearch.executor.CopyExecutor;
import com.enonic.xp.repo.impl.elasticsearch.executor.StoreExecutor;
import com.enonic.xp.repo.impl.elasticsearch.result.GetResultFactory;
import com.enonic.xp.repo.impl.elasticsearch.result.GetResultsFactory;
import com.enonic.xp.repo.impl.storage.CopyRequest;
import com.enonic.xp.repo.impl.storage.DeleteRequest;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByIdsRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.GetResults;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;

@Component
public class StorageDaoImpl
    implements StorageDao
{
    private RestHighLevelClient client;

    @Override
    public String store( final StoreRequest request )
    {
        final StorageSource settings = request.getSettings();

        final IndexRequestBuilder indexRequestBuilder = this.client.prepareIndex().
            setIndex( settings.getStorageName().getName() ).
            setType( settings.getStorageType().getName() ).
            setSource( XContentBuilderFactory.create( request ) ).
            setId( request.getId() );

        if ( request.isForceRefresh() )
        {
            indexRequestBuilder.setRefreshPolicy( WriteRequest.RefreshPolicy.IMMEDIATE );
        }

        if ( request.getRouting() != null )
        {
            indexRequestBuilder.setRouting( request.getRouting() );
        }

        if ( request.getParent() != null )
        {
//            indexRequest.parent( request.getParent() ); TODO ES: needs discussed
        }

        return doStore( indexRequestBuilder.request(), request.getTimeout() );
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

        final DeleteRequestBuilder builder = client.prepareDelete().
            setId( id ).
            setIndex( settings.getStorageName().getName() ).
            setType( settings.getStorageType().getName() );
        if ( request.isForceRefresh() )
        {
            builder.setRefreshPolicy( WriteRequest.RefreshPolicy.IMMEDIATE );
        }

        final DeleteResponse deleteResponse;
        try
        {
            deleteResponse = builder.get( request.getTimeoutAsString() );
        }
        catch ( ClusterBlockException e )
        {
            throw new NodeStorageException( "Cannot delete node " + id + ", Repository in 'READ-ONLY mode'" );
        }
        catch ( Exception e )
        {
            throw new NodeStorageException( "Cannot delete node " + id, e );
        }

        return deleteResponse.status() != RestStatus.NOT_FOUND; // TODO ES: needs double check what is replacement of deleteResponse.isFound()
    }

    @Override
    public void delete( final DeleteRequests requests )
    {
        final StorageSource settings = requests.getSettings();

        for ( final String id : requests.getIds() )
        {
            try
            {
                final DeleteRequestBuilder deleteRequestBuilder = this.client.prepareDelete().
                    setIndex( settings.getStorageName().getName() ).
                    setType( settings.getStorageType().getName() ).
                    setId( id ).
                    setRouting( id );
                if ( requests.isForceRefresh() )
                {
                    deleteRequestBuilder.setRefreshPolicy( WriteRequest.RefreshPolicy.IMMEDIATE );
                }
                deleteRequestBuilder.get( requests.getTimeoutAsString() );
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

    private String doStore( final IndexRequest request, final String timeout )
    {
        final IndexResponse indexResponse;
        try
        {
            indexResponse = this.client.index( request ).
                actionGet( timeout );
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

        final GetRequestBuilder builder = this.client.prepareGet().
            setType( storageSource.getStorageType().getName() ).
            setPreference( request.getSearchPreference().getName() ).
            setId( request.getId() );

        if ( request.getReturnFields().isNotEmpty() )
        {
            builder.setStoredFields( request.getReturnFields().getReturnFieldNames() );
        }

        if ( request.getRouting() != null )
        {
            builder.setRouting( request.getRouting() );
        }

        final GetResponse getResponse = builder.get( request.getTimeout() );

        return GetResultFactory.create( getResponse );
    }

    @Override
    public GetResults getByIds( final GetByIdsRequest requests )
    {
        if ( requests.getRequests().isEmpty() )
        {
            return new GetResults();
        }

        final MultiGetRequestBuilder multiGetRequestBuilder = this.client.prepareMultiGet();

        for ( final GetByIdRequest request : requests.getRequests() )
        {
            final StorageSource storageSource = request.getStorageSource();

            final MultiGetRequest.Item item =
                new MultiGetRequest.Item( storageSource.getStorageName().getName(), storageSource.getStorageType().getName(),
                                          request.getId() );

            if ( request.getReturnFields().isNotEmpty() )
            {
                item.storedFields( request.getReturnFields().getReturnFieldNames() );
            }

            if ( request.getRouting() != null )
            {
                item.routing( request.getRouting() );
            }

            multiGetRequestBuilder.add( item );

        }

        final MultiGetResponse multiGetItemResponses = multiGetRequestBuilder.get();

        return GetResultsFactory.create( multiGetItemResponses );
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
    public void setClient( final RestHighLevelClient client )
    {
        this.client = client;
    }
}
