package com.enonic.wem.repo.internal.elasticsearch.storage;

import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.repo.internal.elasticsearch.SearchRequestBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.result.GetResultFactory;
import com.enonic.wem.repo.internal.elasticsearch.result.SearchResultFactory;
import com.enonic.wem.repo.internal.index.IndexException;
import com.enonic.wem.repo.internal.storage.DeleteRequest;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.GetByValuesRequest;
import com.enonic.wem.repo.internal.storage.SearchPreference;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StorageData;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.SearchResult;

@Component
public class ElasticsearchStorageDao
    implements StorageDao
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchStorageDao.class );

    private Client client;

    @Override
    public String store( final StoreRequest request )
    {
        final StorageSettings settings = request.getSettings();
        final StorageData data = request.getData();

        final IndexRequest indexRequest = Requests.indexRequest().
            index( settings.getStorageName().getName() ).
            type( settings.getStorageType().getName() ).
            source( XContentBuilderFactory.create( request ) ).
            id( request.getId() ).
            refresh( request.isForceRefresh() );

        if ( data.getRouting() != null )
        {
            indexRequest.routing( data.getRouting() );
        }

        if ( data.getParent() != null )
        {
            indexRequest.parent( data.getParent() );
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
            build();

        // TODO: Resolve size

        final SearchRequestBuilder searchRequest = SearchRequestBuilderFactory.newFactory().
            query( query ).
            client( this.client ).
            resolvedSize( request.expectSingleValue() ? 1 : 1000 ).
            build().
            create();

        return doSearchRequest( searchRequest, request.getTimeout(), request.getSearchPreference() );
    }


    private SearchResult doSearchRequest( final SearchRequestBuilder searchRequestBuilder, final String timeout,
                                          final SearchPreference searchPreference )
    {
        try
        {
            final SearchResponse searchResponse = searchRequestBuilder.
                setPreference( searchPreference.getName() ).
                execute().
                actionGet( timeout );

            return SearchResultFactory.create( searchResponse );
        }
        catch ( ElasticsearchException e )
        {
            LOG.error( "Search request failed", e.getRootCause() );

            throw new IndexException( "Search request failed", e );
        }
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

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
