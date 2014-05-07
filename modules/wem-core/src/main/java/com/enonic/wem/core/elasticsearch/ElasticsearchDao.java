package com.enonic.wem.core.elasticsearch;

import java.util.Collection;

import javax.inject.Inject;

import org.elasticsearch.ElasticsearchException;
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
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.enonic.wem.core.elasticsearch.store.AbstractByQuery;
import com.enonic.wem.core.elasticsearch.store.ByIdQuery;
import com.enonic.wem.core.elasticsearch.store.ByIdsQuery;
import com.enonic.wem.core.elasticsearch.store.ByParentPathQuery;
import com.enonic.wem.core.elasticsearch.store.ByPathQuery;
import com.enonic.wem.core.elasticsearch.store.ByPathsQuery;
import com.enonic.wem.core.index.DeleteDocument;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.document.IndexDocument;

public class ElasticsearchDao
{
    public static final boolean DEFAULT_REFRESH = true;

    private Client client;

    public void store( Collection<IndexDocument> indexDocuments )
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

    public String store( final NodeStorageDocument nodeStorageDocument )
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


    public SearchResponse get( final ByIdsQuery byIdsQuery )
    {
        final IdsQueryBuilder idsQueryBuilder = new IdsQueryBuilder();

        for ( final DocumentId documentId : byIdsQuery.getDocumentIds() )
        {
            idsQueryBuilder.addIds( documentId.getId() );
        }

        return doSearchRequest( byIdsQuery, idsQueryBuilder );
    }

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

}
