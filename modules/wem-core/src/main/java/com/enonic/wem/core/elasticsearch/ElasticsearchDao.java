package com.enonic.wem.core.elasticsearch;

import java.util.Collection;

import javax.inject.Inject;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.core.elasticsearch.result.SearchResult;
import com.enonic.wem.core.elasticsearch.result.SearchResultFactory;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.document.IndexDocument;

public class ElasticsearchDao
{
    private static final boolean DEFAULT_REFRESH = true;

    private Client client;

    public void update( final IndexRequest indexRequest )
    {
        UpdateRequestBuilder updateRequestBuilder = new UpdateRequestBuilder( this.client ).
            setIndex( indexRequest.index() ).
            setType( indexRequest.type() ).
            setId( indexRequest.id() ).
            setDoc( indexRequest );

        this.client.update( updateRequestBuilder.request() ).actionGet();
    }

    public void store( final IndexRequest indexRequest )
    {
        this.client.index( indexRequest ).actionGet();
    }

    public void storeAll( final Collection<IndexRequest> indexRequests )
    {
        for ( final IndexRequest indexRequest : indexRequests )
        {
            this.client.index( indexRequest ).actionGet();
        }
    }

    public void store( Collection<IndexDocument> indexDocuments )
    {
        for ( IndexDocument indexDocument : indexDocuments )
        {
            final String id = indexDocument.getId();

            final XContentBuilder xContentBuilder = XContentBuilderFactory.create( indexDocument );

            final IndexRequest req = Requests.indexRequest().
                id( id ).
                index( indexDocument.getIndexName() ).
                type( indexDocument.getIndexTypeName() ).
                source( xContentBuilder ).
                refresh( indexDocument.doRefreshOnStore() );

            this.client.index( req ).actionGet();
        }
    }

    public boolean delete( final DeleteRequest deleteRequest )
    {
        final DeleteResponse deleteResponse = this.client.delete( deleteRequest ).actionGet();
        return deleteResponse.isFound();
    }


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

    public SearchResult search( final ElasticsearchQuery elasticsearchQuery )
    {
        final SearchSourceBuilder searchSource = elasticsearchQuery.toSearchSourceBuilder();

        //System.out.println( searchSource.toString() );

        final SearchRequest searchRequest = Requests.
            searchRequest( elasticsearchQuery.getIndexName() ).
            types( elasticsearchQuery.getIndexType().getName() ).
            source( searchSource );

        return doSearchRequest( searchRequest );
    }

    private SearchResult doSearchRequest( SearchRequest searchRequest )
    {
        final SearchResponse searchResponse = this.client.search( searchRequest ).actionGet();

        return SearchResultFactory.create( searchResponse );
    }

    public SearchResult get( final QueryMetaData queryMetaData, final QueryBuilder queryBuilder )
    {
        final SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch( queryMetaData.getIndex() ).
            setTypes( queryMetaData.getIndexType() ).
            setQuery( queryBuilder ).
            setFrom( queryMetaData.getFrom() ).
            setSize( queryMetaData.getSize() );

        final ImmutableSet<SortBuilder> sortBuilders = queryMetaData.getSortBuilders();
        if ( !sortBuilders.isEmpty() )
        {
            for ( final SortBuilder sortBuilder : sortBuilders )
            {
                searchRequestBuilder.addSort( sortBuilder );
            }
        }

        if ( queryMetaData.hasFields() )
        {
            searchRequestBuilder.addFields( queryMetaData.getFields() );
        }

        return doSearchRequest( searchRequestBuilder );
    }

    public SearchResult get( final QueryMetaData queryMetaData, final String id )
    {
        final GetRequest getRequest = new GetRequest( queryMetaData.getIndex() ).
            type( queryMetaData.getIndexType() ).
            id( id );

        if ( queryMetaData.hasFields() )
        {
            getRequest.fields( queryMetaData.getFields() );
        }

        final GetResponse getResponse = client.get( getRequest ).actionGet();

        return SearchResultFactory.create( getResponse );
    }

    public long count( final QueryMetaData queryMetaData, final QueryBuilder query )
    {
        CountRequestBuilder countRequestBuilder = new CountRequestBuilder( this.client ).
            setIndices( queryMetaData.getIndex() ).
            setTypes( queryMetaData.getIndexType() ).
            setQuery( query );

        final CountResponse response = this.client.count( countRequestBuilder.request() ).actionGet();
        return response.getCount();
    }

    private SearchResult doSearchRequest( final SearchRequestBuilder searchRequestBuilder )
    {
        final SearchResponse searchResponse = searchRequestBuilder.
            execute().
            actionGet();

        return SearchResultFactory.create( searchResponse );
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }

}
