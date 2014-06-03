package com.enonic.wem.core.elasticsearch;

import java.util.List;
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

import com.google.common.collect.Sets;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobKeys;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.workspace.UpdateWorkspaceReferenceDocument;
import com.enonic.wem.core.workspace.WorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceStore;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceDiffQuery;
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

    //  @Override
    public void newStore( final WorkspaceDocument workspaceDocument )
    {
        final String workspaceDocumentId = newGenerateWorkspaceDocumentId( workspaceDocument );

        final GetResponse existingDocument = getByWorkspaceDocumentId( workspaceDocumentId );

        if ( existingDocument.isExists() )
        {
            final List<Object> existingWorkspaceRefs = existingDocument.getField( WORKSPACE_FIELD_NAME ).getValues();

            final boolean alreadyReferredFromWorkspace = existingWorkspaceRefs.contains( workspaceDocument.getWorkspace().toString() );
            if ( alreadyReferredFromWorkspace )
            {
                return;
            }
            else // Remove old reference and update document with new workspace
            {
                removeOldReferenceIfExists( workspaceDocument.getEntityId(), workspaceDocument.getWorkspace() );
                updateWithNewWorkspaceRef( workspaceDocumentId, workspaceDocument.getWorkspace().toString(), existingWorkspaceRefs );
            }
        }
        else
        {
            removeOldReferenceIfExists( workspaceDocument.getEntityId(), workspaceDocument.getWorkspace() );
            storeNewDocument( workspaceDocument );
        }
    }

    private void removeOldReferenceIfExists( final EntityId entityId, final Workspace workspace )
    {
        final SearchHit documentWithReference = doGetById( entityId, workspace, WORKSPACE_FIELD_NAME );

        if ( documentWithReference == null )
        {
            return;
        }

        final String id = documentWithReference.getId();
        final List<Object> existingWorkspaceRefs = SearchResponseAccessor.getMultipleValues( documentWithReference, WORKSPACE_FIELD_NAME );

        if ( existingWorkspaceRefs.contains( workspace.getName() ) )
        {
            removeWorkspaceRef( id, workspace.getName(), existingWorkspaceRefs );
        }
    }


    private void updateWithNewWorkspaceRef( final String documentId, final String newWorkspaceRef,
                                            final List<Object> existingWorkspaceRefs )
    {
        final UpdateWorkspaceReferenceDocument updateDocument = UpdateWorkspaceReferenceDocument.
            create().
            addAll( existingWorkspaceRefs ).
            add( newWorkspaceRef ).
            build();

        updateWorkspaceRef( documentId, updateDocument );
    }

    private void removeWorkspaceRef( final String documentId, final String toBeRemovedRefs, final List<Object> existingWorkspaceRefs )
    {
        final UpdateWorkspaceReferenceDocument updateDocument = UpdateWorkspaceReferenceDocument.
            create().
            addAll( existingWorkspaceRefs ).
            remove( toBeRemovedRefs ).
            build();

        updateWorkspaceRef( documentId, updateDocument );
    }

    private void updateWorkspaceRef( final String documentId, final UpdateWorkspaceReferenceDocument updateDocument )
    {
        final IndexRequest indexRequest = Requests.indexRequest().
            index( WORKSPACE_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            source( WorkspaceXContentBuilderFactory.create( updateDocument ) ).
            id( documentId ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.update( indexRequest );
    }

    private GetResponse getByWorkspaceDocumentId( final String workspaceDocumentId )
    {
        return elasticsearchDao.get( QueryMetaData.
            create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            addField( WORKSPACE_FIELD_NAME ).
            build(), workspaceDocumentId );
    }

    private void storeNewDocument( final WorkspaceDocument workspaceDocument )
    {
        final String workspaceDocumentId = newGenerateWorkspaceDocumentId( workspaceDocument );

        final IndexRequest indexRequest = Requests.indexRequest().
            index( WORKSPACE_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            source( WorkspaceXContentBuilderFactory.create( workspaceDocument ) ).
            id( workspaceDocumentId ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( indexRequest );
    }

    private String generateWorkspaceDocumentId( final WorkspaceDocument workspaceDocument )
    {
        return newGenerateWorkspaceDocumentId( workspaceDocument.getEntityId().toString(), workspaceDocument.getWorkspace().getName() );
    }

    private String generateWorkspaceDocumentId( final String entityId, final String workspaceName )
    {
        return entityId + "-" + workspaceName;
    }

    private String newGenerateWorkspaceDocumentId( final WorkspaceDocument workspaceDocument )
    {
        return newGenerateWorkspaceDocumentId( workspaceDocument.getEntityId().toString(), workspaceDocument.getBlobKey().toString() );
    }

    private String newGenerateWorkspaceDocumentId( final String entityId, final String blobKey )
    {
        return entityId + "-" + blobKey;
    }

    // @Override
    public void newDelete( final WorkspaceDeleteQuery query )
    {
        removeOldReferenceIfExists( query.getEntityId(), query.getWorkspace() );

        // TODO: Should old docs be deleted? Maybe moved into versions-index?
       /* DeleteRequest deleteRequest = new DeleteRequest( WORKSPACE_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            id( generateWorkspaceDocumentId( query.getEntityId().toString(), query.getWorkspace().getName() ) ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.delete( deleteRequest );
        */
    }


    @Override
    public void delete( final WorkspaceDeleteQuery query )
    {
        DeleteRequest deleteRequest = new DeleteRequest( WORKSPACE_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            id( newGenerateWorkspaceDocumentId( query.getEntityId().toString(), query.getWorkspace().getName() ) ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.delete( deleteRequest );
    }

    @Override
    public BlobKey getById( final WorkspaceIdQuery query )
    {
        final EntityId entityId = query.getEntityId();

        final SearchHit hit = doGetById( entityId, query.getWorkspace(), BLOBKEY_FIELD_NAME );

        if ( hit == null )
        {
            throw new NodeNotFoundException( "Node with id: " + entityId + " not found in workspace " + query.getWorkspace().getName() );
        }

        final Object value = SearchResponseAccessor.getFieldValue( hit, WorkspaceXContentBuilderFactory.BLOBKEY_FIELD_NAME );

        if ( value == null )
        {
            throw new RuntimeException( "Field " + BLOBKEY_FIELD_NAME + " not found on node with id " +
                                            entityId +
                                            " in workspace " + query.getWorkspace().getName() );
        }

        return new BlobKey( value.toString() );
    }

    private SearchHit doGetById( final EntityId entityId, final Workspace workspace, final String field )
    {
        return doGetById( entityId, workspace, Sets.newHashSet( field ) );
    }

    private SearchHit doGetById( final EntityId entityId, final Workspace workspace, final Set<String> fields )
    {
        final TermQueryBuilder idQuery = new TermQueryBuilder( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME, entityId.toString() );

        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspace.getName(), idQuery );

        final QueryMetaData queryMetaData = QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( 1 ).
            addFields( fields ).
            build();

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        return SearchResponseAccessor.getSingleHit( searchResponse );
    }

    @Override
    public BlobKeys getByIds( final WorkspaceIdsQuery query )
    {
        final Set<String> entityIdsAsStrings = query.getEntityIdsAsStrings();
        final int expectedHits = entityIdsAsStrings.size();

        return doGetByIds( query.getWorkspace(), entityIdsAsStrings, expectedHits );
    }

    private BlobKeys doGetByIds( final Workspace workspace, final Set<String> entityIdsAsStrings, final int expectedHits )
    {
        final String workspaceName = workspace.getName();

        final TermsQueryBuilder idsQuery = new TermsQueryBuilder( ENTITY_ID_FIELD_NAME, entityIdsAsStrings );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspaceName, idsQuery );

        final QueryMetaData queryMetaData = createGetBlobkeyQueryMetaData( entityIdsAsStrings.size() );

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        final SearchHit[] hits = searchResponse.getHits().hits();

        if ( hits.length < expectedHits )
        {
            throw new NodeNotFoundException(
                "Expected " + expectedHits + " nodes in result, found " + hits.length + " in workspace " + workspaceName +
                    ". Query: " + entityIdsAsStrings
            );
        }

        final Set<Object> fieldValues = SearchResponseAccessor.getFieldValues( hits, BLOBKEY_FIELD_NAME );

        if ( fieldValues.size() < expectedHits )
        {
            throw new RuntimeException( "Field " + BLOBKEY_FIELD_NAME + " not found on one or more nodes with ids " +
                                            entityIdsAsStrings +
                                            " in workspace " + workspaceName );
        }

        return fieldValuesToBlobKeys( fieldValues );
    }

    @Override
    public BlobKey getByPath( final WorkspacePathQuery query )
    {
        final TermQueryBuilder parentQuery = new TermQueryBuilder( PATH_FIELD_NAME, query.getNodePathAsString() );
        final BoolQueryBuilder workspacedByPathQuery = joinWithWorkspaceQuery( query.getWorkspace().getName(), parentQuery );

        final QueryMetaData queryMetaData = createGetBlobkeyQueryMetaData( 1 );

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, workspacedByPathQuery );

        final SearchHit hit = SearchResponseAccessor.getSingleHit( searchResponse );

        if ( hit == null )
        {
            throw new NodeNotFoundException( "Node with path: " + query.getNodePathAsString() + " not found in workspace " +
                                                 query.getWorkspace() );
        }

        final Object value = SearchResponseAccessor.getFieldValue( hit, BLOBKEY_FIELD_NAME );

        if ( value == null )
        {
            throw new RuntimeException( "Field " + BLOBKEY_FIELD_NAME + " not found on node with path " +
                                            query.getNodePathAsString() +
                                            " in workspace " + query.getWorkspace() );
        }

        return new BlobKey( value.toString() );
    }

    @Override
    public BlobKeys getByPaths( final WorkspacePathsQuery query )
    {
        final TermsQueryBuilder parentQuery = new TermsQueryBuilder( PATH_FIELD_NAME, query.getNodePathsAsStrings() );
        final BoolQueryBuilder workspacedByPathsQuery = joinWithWorkspaceQuery( query.getWorkspace().getName(), parentQuery );

        final QueryMetaData queryMetaData = createGetBlobkeyQueryMetaData( query.getNodePathsAsStrings().size() );

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
        final BoolQueryBuilder workspacedByParentQuery = joinWithWorkspaceQuery( query.getWorkspace().getName(), parentQuery );

        final QueryMetaData queryMetaData = createGetBlobkeyQueryMetaData( DEFAULT_UNKNOWN_SIZE );

        final SearchResponse searchResponse = elasticsearchDao.get( queryMetaData, workspacedByParentQuery );

        if ( searchResponse.getHits().getTotalHits() == 0 )
        {
            return BlobKeys.empty();
        }

        final Set<Object> fieldValues =
            SearchResponseAccessor.getFieldValues( searchResponse.getHits().getHits(), WorkspaceXContentBuilderFactory.BLOBKEY_FIELD_NAME );

        return fieldValuesToBlobKeys( fieldValues );
    }

    public EntityIds getDiff( final WorkspaceDiffQuery query )
    {
        BoolQueryBuilder diffQuery = new BoolQueryBuilder();

        BoolQueryBuilder inAtLeastOne = new BoolQueryBuilder();
        TermQueryBuilder inSource = new TermQueryBuilder( WORKSPACE_FIELD_NAME, query.getSource().getName() );
        TermQueryBuilder inTarget = new TermQueryBuilder( WORKSPACE_FIELD_NAME, query.getTarget().getName() );
        inAtLeastOne.should( inSource ).should( inTarget ).minimumNumberShouldMatch( 1 );

        BoolQueryBuilder inBoth = new BoolQueryBuilder();
        inBoth.must( inSource ).must( inTarget );

        diffQuery.must( inAtLeastOne ).mustNot( inBoth );

        final SearchResponse searchResponse = elasticsearchDao.get( createGetBlobkeyQueryMetaData( DEFAULT_UNKNOWN_SIZE ), diffQuery );

        return null;
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

    private QueryMetaData createGetBlobkeyQueryMetaData( final int numberOfHits )
    {
        return QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( numberOfHits ).
            addField( BLOBKEY_FIELD_NAME ).build();
    }

    private BoolQueryBuilder joinWithWorkspaceQuery( final String workspaceName, final QueryBuilder specificQuery )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        final TermQueryBuilder workspaceQuery = new TermQueryBuilder( WORKSPACE_FIELD_NAME, workspaceName );
        boolQueryBuilder.must( specificQuery );
        boolQueryBuilder.must( workspaceQuery );

        return boolQueryBuilder;
    }

}
