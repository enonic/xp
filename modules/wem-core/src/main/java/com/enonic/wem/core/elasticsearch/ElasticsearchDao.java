package com.enonic.wem.core.elasticsearch;

import java.util.Collection;

import javax.inject.Inject;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.core.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.core.elasticsearch.result.SearchResultFactory;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.document.IndexDocument;
import com.enonic.wem.core.index.result.SearchResult;

public class ElasticsearchDao
{
    private final static Logger LOG = LoggerFactory.getLogger( ElasticsearchDao.class );

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
            type( deleteDocument.getIndexType() ).
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
                                          deleteDocument.getIndexType() + " with id " + deleteDocument.getId(), e );
        }
    }

    public SearchResult search( final ElasticsearchQuery elasticsearchQuery )
    {
        final SearchSourceBuilder searchSource = elasticsearchQuery.toSearchSourceBuilder();

        System.out.println( searchSource.toString() );

        final SearchRequest searchRequest = Requests.
            searchRequest( elasticsearchQuery.getIndexName() ).
            types( elasticsearchQuery.getIndexType() ).
            source( searchSource );

        return doSearchRequest( searchRequest );
    }

    private SearchResult doSearchRequest( final SearchRequest searchRequest )
    {
        final SearchResponse searchResponse;
        try
        {
            searchResponse = this.client.search( searchRequest ).actionGet();
        }
        catch ( IndexMissingException e )
        {
            // TODO: Index must be injected here, as dynamic indexes should not cause exception if not existing

            //if ( index.isDynamic() )
            //{
            LOG.warn( "Indices does not exist", searchRequest.indices() );
            return SearchResult.create().build();
            //}
            //else
            //{
            //    throw new IndexException( "Index " + index.name() + " does not exist" );
            //}

        }

        return SearchResultFactory.create( searchResponse );
    }

    public SearchResult get( final QueryMetaData queryMetaData, final QueryBuilder queryBuilder )
    {
        final SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch( queryMetaData.getIndexName() ).
            setTypes( queryMetaData.getIndexTypeName() ).
            setQuery( queryBuilder ).
            setFrom( queryMetaData.getFrom() ).
            setSize( queryMetaData.getSize() );

        final ImmutableSet<SortBuilder> sortBuilders = queryMetaData.getSortBuilders();
        if ( !sortBuilders.isEmpty() )
        {
            sortBuilders.forEach( searchRequestBuilder::addSort );
        }

        if ( queryMetaData.hasFields() )
        {
            searchRequestBuilder.addFields( queryMetaData.getFields() );
        }

        return doSearchRequest( searchRequestBuilder );
    }

    public SearchResult get( final QueryMetaData queryMetaData, final String id )
    {
        final GetRequest getRequest = new GetRequest( queryMetaData.getIndexName() ).
            type( queryMetaData.getIndexTypeName() ).
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
        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder( this.client ).
            setIndices( queryMetaData.getIndexName() ).
            setTypes( queryMetaData.getIndexTypeName() ).
            setQuery( query ).
            setSearchType( SearchType.COUNT );

        final SearchResponse searchResponse = this.client.search( searchRequestBuilder.request() ).actionGet();

        return searchResponse.getHits().getTotalHits();
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
