package com.enonic.xp.repo.impl.elasticsearch.storage;

import java.util.Map;

import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.elasticsearch.ElasticsearchDao;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.result.GetResultFactory;
import com.enonic.xp.repo.impl.elasticsearch.result.GetResultsFactory;
import com.enonic.xp.repo.impl.search.SearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.DeleteRequest;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByIdsRequest;
import com.enonic.xp.repo.impl.storage.GetByValuesRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.GetResults;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;

@Component
public class ElasticsearchStorageDao
    implements StorageDao
{
    private Client client;

    private ElasticsearchDao elasticsearchDao;

    @Override
    public String store( final StoreRequest request )
    {
        final StorageSettings settings = request.getSettings();

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
    public boolean delete( final DeleteRequest request )
    {
        final StorageSettings settings = request.getSettings();
        final String id = request.getId();

        final DeleteRequestBuilder builder = new DeleteRequestBuilder( this.client ).
            setId( id ).
            setIndex( settings.getStorageName().getName() ).
            setType( settings.getStorageType().getName() ).
            setRefresh( request.isForceRefresh() );

        final DeleteResponse deleteResponse = this.client.delete( builder.request() ).
            actionGet( request.getTimeoutAsString() );

        return deleteResponse.isFound();
    }

    private String doStore( final IndexRequest request, final String timeout )
    {
        final IndexResponse indexResponse = this.client.index( request ).
            actionGet( timeout );

        return indexResponse.getId();
    }

    @Override
    public SearchResult getByValues( final GetByValuesRequest request )
    {
        final Map<String, Object> values = request.getValues();
        final StorageSettings settings = request.getStorageSettings();

        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        for ( final String field : values.keySet() )
        {
            final TermQueryBuilder termQuery = new TermQueryBuilder( field, values.get( field ) );
            boolQueryBuilder.must( termQuery );
        }

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( settings.getStorageName().getName() ).
            indexType( settings.getStorageType().getName() ).
            query( boolQueryBuilder ).
            size( request.expectSingleValue() ? 1 : SearchService.GET_ALL_SIZE_FLAG ).
            setReturnFields( request.getReturnFields() ).
            build();

        return this.elasticsearchDao.search( query );
    }

    @Override
    public GetResult getById( final GetByIdRequest request )
    {
        final StorageSettings storageSettings = request.getStorageSettings();
        final GetRequest getRequest = new GetRequest( storageSettings.getStorageName().getName() ).
            type( storageSettings.getStorageType().getName() ).
            preference( request.getSearchPreference().getName() ).
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
            actionGet( request.getTimeout() );

        return GetResultFactory.create( getResponse );
    }

    @Override
    public GetResults getByIds( final GetByIdsRequest requests )
    {
        if ( requests.getRequests().isEmpty() )
        {
            return new GetResults();
        }

        final MultiGetRequestBuilder multiGetRequestBuilder = new MultiGetRequestBuilder( this.client );

        for ( final GetByIdRequest request : requests.getRequests() )
        {
            final StorageSettings storageSettings = request.getStorageSettings();

            final MultiGetRequest.Item item =
                new MultiGetRequest.Item( storageSettings.getStorageName().getName(), storageSettings.getStorageType().getName(),
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

        return GetResultsFactory.create( multiGetItemResponses );
    }


    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
