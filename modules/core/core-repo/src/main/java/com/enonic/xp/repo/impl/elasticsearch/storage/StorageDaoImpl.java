package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.util.Collection;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
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
    private EsClient client;

    @Override
    public String store( final StoreRequest request )
    {
        final StorageSource settings = request.getSettings();

        final IndexRequest indexRequest = new IndexRequest().
            index( settings.getStorageName().getName() ).
            source( XContentBuilderFactory.create( request ) ).
            id( request.getId() ).
            timeout( request.getTimeout() );

        if ( request.isForceRefresh() )
        {
            indexRequest.setRefreshPolicy( WriteRequest.RefreshPolicy.IMMEDIATE );
        }
        if ( request.getRouting() != null )
        {
            indexRequest.routing( request.getRouting() );
        }
        if ( request.getParent() != null )
        {
            indexRequest.routing( request.getParent() );
        }

        return doStore( indexRequest );
    }

    @Override
    public void store( final Collection<IndexDocument> indexDocuments )
    {
        StoreExecutor.create( this.client ).
            build().
            execute( indexDocuments );
    }

    @Override
    public void delete( final DeleteRequest request )
    {
        final StorageSource settings = request.getSettings();
        final String id = request.getId();

        final org.elasticsearch.action.delete.DeleteRequest deleteRequest = new org.elasticsearch.action.delete.DeleteRequest().
            id( id ).
            index( settings.getStorageName().getName() ).
            timeout( request.getTimeoutAsString() );

        if ( request.isForceRefresh() )
        {
            deleteRequest.setRefreshPolicy( WriteRequest.RefreshPolicy.IMMEDIATE );
        }

        try
        {
            client.delete( deleteRequest );
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

    @Override
    public void delete( final DeleteRequests requests )
    {
        final StorageSource settings = requests.getSettings();

        for ( final String id : requests.getIds() )
        {
            try
            {
                final org.elasticsearch.action.delete.DeleteRequest deleteRequest = new org.elasticsearch.action.delete.DeleteRequest().
                    id( id ).
                    index( settings.getStorageName().getName() ).
                    timeout( requests.getTimeoutAsString() );

                if ( requests.isForceRefresh() )
                {
                    deleteRequest.setRefreshPolicy( WriteRequest.RefreshPolicy.IMMEDIATE );
                }

                client.delete( deleteRequest );
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

    private String doStore( final IndexRequest request )
    {
        try
        {
            final IndexResponse indexResponse = this.client.index( request );
            return indexResponse.getId();
        }
        catch ( ClusterBlockException e )
        {
            throw new NodeStorageException( "Cannot store node " + request.id() + ", Repository in 'READ-ONLY mode'" );
        }
        catch ( ElasticsearchException e )
        {
            throw new NodeStorageException( "Cannot store node " + request.toString(), e );
        }
    }

    @Override
    public GetResult getById( final GetByIdRequest request )
    {
        final StorageSource storageSource = request.getStorageSource();

        final GetRequest getRequest = new GetRequest().
            index( storageSource.getStorageName().getName() ).
            id( request.getId() ).
            preference( request.getSearchPreference().getName() );

        if ( request.getReturnFields().isNotEmpty() )
        {
            getRequest.fetchSourceContext(
                new FetchSourceContext( true, request.getReturnFields().getReturnFieldNames(), Strings.EMPTY_ARRAY ) );
        }

        if ( request.getRouting() != null )
        {
            getRequest.routing( request.getRouting() );
        }

        GetResponse response = client.get( getRequest );
        return GetResultFactory.create( response );
    }

    @Override
    public GetResults getByIds( final GetByIdsRequest requests )
    {
        if ( requests.getRequests().isEmpty() )
        {
            return new GetResults();
        }

        final MultiGetRequest multiGetRequest = new MultiGetRequest();

        for ( final GetByIdRequest request : requests.getRequests() )
        {
            final StorageSource storageSource = request.getStorageSource();

            final MultiGetRequest.Item item = new MultiGetRequest.Item( storageSource.getStorageName().getName(), request.getId() );

            if ( request.getReturnFields().isNotEmpty() )
            {
                item.fetchSourceContext(
                    new FetchSourceContext( true, request.getReturnFields().getReturnFieldNames(), Strings.EMPTY_ARRAY ) );
            }

            if ( request.getRouting() != null )
            {
                item.routing( request.getRouting() );
            }

            multiGetRequest.add( item );
        }

        final MultiGetResponse response = client.mget( multiGetRequest );
        return GetResultsFactory.create( response );
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
    public void setClient( final EsClient client )
    {
        this.client = client;
    }
}
