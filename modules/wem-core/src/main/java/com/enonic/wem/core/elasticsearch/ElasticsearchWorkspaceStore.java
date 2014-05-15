package com.enonic.wem.core.elasticsearch;

import java.util.Set;

import javax.inject.Inject;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobKeys;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.workspace.WorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceStore;
import com.enonic.wem.core.workspace.query.AbstractWorkspaceQuery;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathsQuery;

import static com.enonic.wem.core.elasticsearch.WorkspaceXContentBuilderFactory.BLOBKEY_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.WorkspaceXContentBuilderFactory.PARENT_PATH_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.WorkspaceXContentBuilderFactory.PATH_FIELD_NAME;
import static com.enonic.wem.core.elasticsearch.WorkspaceXContentBuilderFactory.WORKSPACE_FIELD_NAME;

public class ElasticsearchWorkspaceStore
    implements WorkspaceStore
{
    private final static Index WORKSPACE_INDEX = Index.WORKSPACE;

    private static final boolean DEFAULT_REFRESH = true;

    private static final int DEFAULT_UNKNOWN_SIZE = 1000;

    @Inject
    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final WorkspaceDocument workspaceDocument )
    {
        final String workspaceDocumentId = generateWorkspaceDocumentId( workspaceDocument );

        if ( unchanged( workspaceDocument, workspaceDocumentId ) )
        {
            return;
        }

        final IndexRequest indexRequest = Requests.indexRequest().
            index( WORKSPACE_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            source( WorkspaceXContentBuilderFactory.create( workspaceDocument ) ).
            id( workspaceDocumentId ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( indexRequest );
    }

    private boolean unchanged( final WorkspaceDocument workspaceDocument, final String workspaceDocumentId )
    {
        final GetResponse result = elasticsearchDao.get( QueryMetaData.
            create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            addField( BLOBKEY_FIELD_NAME ).
            addField( PARENT_PATH_FIELD_NAME ).
            addField( PATH_FIELD_NAME ).
            build(), workspaceDocumentId );

        if ( result.isExists() )
        {
            final String currentBlobKey = result.getField( BLOBKEY_FIELD_NAME ).getValue().toString();
            final String currentParentPath = result.getField( PARENT_PATH_FIELD_NAME ).getValue().toString();
            final String currentPath = result.getField( PATH_FIELD_NAME ).getValue().toString();

            if ( currentBlobKey.equals( workspaceDocument.getBlobKey().toString() ) &&
                currentParentPath.equals( workspaceDocument.getParentPath().toString() ) &&
                currentPath.equals( workspaceDocument.getPath().toString() ) )
            {
                return true;
            }
        }
        return false;
    }

    private String generateWorkspaceDocumentId( final WorkspaceDocument workspaceDocument )
    {
        return generateWorkspaceDocumentId( workspaceDocument.getEntityId().toString(), workspaceDocument.getWorkspaceName() );
    }

    private String generateWorkspaceDocumentId( final String entityId, final String workspaceName )
    {
        return workspaceName + entityId;
    }

    @Override
    public void delete( final WorkspaceDeleteQuery query )
    {
        DeleteRequest deleteRequest = new DeleteRequest( WORKSPACE_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            id( generateWorkspaceDocumentId( query.getEntityId().toString(), query.getWorkspaceName() ) ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.delete( deleteRequest );
    }

    @Override
    public BlobKey getById( final WorkspaceIdQuery query )
    {
        final TermQueryBuilder idQuery =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME, query.getEntityIdAsString() );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( query, idQuery );

        final QueryMetaData queryMetaData = QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( 1 ).
            addField( BLOBKEY_FIELD_NAME ).build();

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        final SearchHit hit = SearchResponseAccessor.getSingleHit( searchResponse );

        if ( hit == null )
        {
            throw new NodeNotFoundException(
                "Node with id: " + query.getEntityIdAsString() + " not found in workspace " + query.getWorkspaceName() );
        }

        final Object value = SearchResponseAccessor.getFieldValue( hit, WorkspaceXContentBuilderFactory.BLOBKEY_FIELD_NAME );

        if ( value == null )
        {
            throw new IllegalArgumentException( "Field " + BLOBKEY_FIELD_NAME + " not found on node with id " +
                                                    query.getEntityIdAsString() +
                                                    " in workspace " + query.getWorkspaceName() );
        }

        return new BlobKey( (String) value );
    }

    @Override
    public BlobKeys getByIds( final WorkspaceIdsQuery query )
    {
        final TermsQueryBuilder idsQuery = new TermsQueryBuilder( ENTITY_ID_FIELD_NAME, query.getEntityIdsAsStrings() );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( query, idsQuery );

        final int expectedHits = query.getEntityIdsAsStrings().size();

        final QueryMetaData queryMetaData = QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( expectedHits ).
            addField( BLOBKEY_FIELD_NAME ).build();

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        final SearchHit[] hits = searchResponse.getHits().hits();

        if ( hits.length < expectedHits )
        {
            throw new NodeNotFoundException(
                "Expected " + expectedHits + " nodes in result, found " + hits.length + " in workspace " + query.getWorkspaceName() +
                    ". Query: " + query.getEntityIdsAsStrings() );
        }

        final Set<Object> fieldValues = SearchResponseAccessor.getFieldValues( hits, BLOBKEY_FIELD_NAME );

        if ( fieldValues.size() < expectedHits )
        {
            throw new IllegalArgumentException( "Field " + BLOBKEY_FIELD_NAME + " not found on one or more nodes with ids " +
                                                    query.getEntityIdsAsStrings() +
                                                    " in workspace " + query.getWorkspaceName() );
        }

        return fieldValuesToBlobKeys( fieldValues );
    }

    @Override
    public BlobKey getByPath( final WorkspacePathQuery query )
    {
        final TermQueryBuilder parentQuery = new TermQueryBuilder( PATH_FIELD_NAME, query.getNodePathAsString() );

        final BoolQueryBuilder workspacedByPathQuery = joinWithWorkspaceQuery( query, parentQuery );

        final QueryMetaData queryMetaData = createOnHitQueryMetaData();

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, workspacedByPathQuery );

        final SearchHit hit = SearchResponseAccessor.getSingleHit( searchResponse );

        if ( hit == null )
        {
            throw new NodeNotFoundException( "Node with path: " + query.getNodePathAsString() + " not found in workspace " +
                                                 query.getWorkspaceName() );
        }

        final Object value = SearchResponseAccessor.getFieldValue( hit, BLOBKEY_FIELD_NAME );

        if ( value == null )
        {
            throw new IllegalArgumentException( "Field " + BLOBKEY_FIELD_NAME + " not found on node with path " +
                                                    query.getNodePathAsString() +
                                                    " in workspace " + query.getWorkspaceName() );
        }

        return new BlobKey( (String) value );
    }

    @Override
    public BlobKeys getByPaths( final WorkspacePathsQuery query )
    {
        final TermsQueryBuilder parentQuery = new TermsQueryBuilder( PATH_FIELD_NAME, query.getNodePathsAsStrings() );

        final BoolQueryBuilder workspacedByPathsQuery = joinWithWorkspaceQuery( query, parentQuery );

        final QueryMetaData queryMetaData = createOnHitQueryMetaData();

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, workspacedByPathsQuery );

        final long totalHits = searchResponse.getHits().getTotalHits();

        if ( totalHits != query.getNodePathsAsStrings().size() )
        {
            throw new RuntimeException( "Expected " + query.getNodePathsAsStrings().size() + " results, got " + totalHits + " for paths " +
                                            query.getNodePathsAsStrings() );
        }

        final Set<Object> fieldValues =
            SearchResponseAccessor.getFieldValues( searchResponse.getHits().hits(), WorkspaceXContentBuilderFactory.BLOBKEY_FIELD_NAME );

        return fieldValuesToBlobKeys( fieldValues );
    }

    @Override
    public BlobKeys getByParent( final WorkspaceParentQuery query )
    {
        final TermQueryBuilder parentQuery = new TermQueryBuilder( PARENT_PATH_FIELD_NAME, query.getParentPath() );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( query, parentQuery );

        final QueryMetaData queryMetaData = createUnknowNumberOfHitsQueryMetaData();

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        if ( searchResponse.getHits().getTotalHits() == 0 )
        {
            return BlobKeys.empty();
        }

        final Set<Object> fieldValues =
            SearchResponseAccessor.getFieldValues( searchResponse.getHits().getHits(), WorkspaceXContentBuilderFactory.BLOBKEY_FIELD_NAME );

        return fieldValuesToBlobKeys( fieldValues );
    }

    private BlobKeys fieldValuesToBlobKeys( final Set<Object> fieldValues )
    {
        final BlobKeys.Builder blobKeysBuilder = BlobKeys.create();
        for ( final Object value : fieldValues )
        {
            blobKeysBuilder.add( new BlobKey( value.toString() ) );
        }
        return blobKeysBuilder.build();
    }


    private QueryMetaData createUnknowNumberOfHitsQueryMetaData()
    {
        return QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( DEFAULT_UNKNOWN_SIZE ).
            addField( BLOBKEY_FIELD_NAME ).build();
    }

    private QueryMetaData createOnHitQueryMetaData()
    {
        return QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( 1 ).
            addField( BLOBKEY_FIELD_NAME ).build();
    }

    private BoolQueryBuilder joinWithWorkspaceQuery( final AbstractWorkspaceQuery query, final QueryBuilder specificQuery )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        final TermQueryBuilder workspaceQuery = new TermQueryBuilder( WORKSPACE_FIELD_NAME, query.getWorkspaceName() );
        boolQueryBuilder.must( specificQuery );
        boolQueryBuilder.must( workspaceQuery );

        return boolQueryBuilder;
    }

}
