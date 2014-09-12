package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.ElasticsearchDataException;
import com.enonic.wem.core.elasticsearch.QueryMetaData;
import com.enonic.wem.core.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.index.Index;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.StorageNameResolver;
import com.enonic.wem.core.workspace.WorkspaceDocument;
import com.enonic.wem.core.workspace.WorkspaceDocumentId;
import com.enonic.wem.core.workspace.WorkspaceService;
import com.enonic.wem.core.workspace.compare.query.CompareWorkspacesQuery;
import com.enonic.wem.core.workspace.query.WorkspaceDeleteQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceIdsQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathQuery;
import com.enonic.wem.core.workspace.query.WorkspacePathsQuery;

public class ElasticsearchWorkspaceService
    implements WorkspaceService
{
    private final static Index WORKSPACE_INDEX = Index.WORKSPACE;

    private static final boolean DEFAULT_REFRESH = true;

    private static final int DEFAULT_UNKNOWN_SIZE = 1000;

    private static final String BUILTIN_TIMESTAMP_FIELD = "_timestamp";

    private ElasticsearchDao elasticsearchDao;

    @Override
    public void store( final WorkspaceDocument workspaceDocument )
    {
        doStore( workspaceDocument );
    }

    private void doStore( final WorkspaceDocument workspaceDocument )
    {
        final WorkspaceDocumentId workspaceDocumentId =
            new WorkspaceDocumentId( workspaceDocument.getEntityId(), workspaceDocument.getWorkspace() );

        final IndexRequest publish = Requests.indexRequest().
            index( StorageNameResolver.resolveStorageIndexName( workspaceDocument.getRepository() ) ).
            type( IndexType.WORKSPACE.getName() ).
            source( WorkspaceXContentBuilderFactory.create( workspaceDocument ) ).
            id( workspaceDocumentId.toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.store( publish );
    }

    @Override
    public void delete( final WorkspaceDeleteQuery query )
    {
        DeleteRequest deleteRequest = new DeleteRequest( WORKSPACE_INDEX.getName() ).
            type( IndexType.NODE.getName() ).
            id( new WorkspaceDocumentId( query.getEntityId(), query.getWorkspace() ).toString() ).
            refresh( DEFAULT_REFRESH );

        elasticsearchDao.delete( deleteRequest );
    }

    @Override
    public NodeVersionId getCurrentVersion( final WorkspaceIdQuery query )
    {
        final EntityId entityId = query.getEntityId();

        final SearchResultEntry searchResultEntry =
            doGetById( entityId, query.getWorkspace(), WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME );

        if ( searchResultEntry == null )
        {
            return null;
        }

        final SearchResultField field = searchResultEntry.getField( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME );

        if ( field == null || field.getValue() == null )
        {
            throw new ElasticsearchDataException(
                "Field " + WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME + " not found on node with id " +
                                                      entityId +
                                                      " in workspace " + query.getWorkspace().getName() );
        }

        return NodeVersionId.from( field.getValue().toString() );
    }

    private SearchResultEntry doGetById( final EntityId entityId, final Workspace workspace, final String field )
    {
        return doGetById( entityId, workspace, Sets.newHashSet( field ) );
    }

    private SearchResultEntry doGetById( final EntityId entityId, final Workspace workspace, final Set<String> fields )
    {
        final TermQueryBuilder idQuery = new TermQueryBuilder( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME, entityId.toString() );

        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspace.getName(), idQuery );

        final QueryMetaData queryMetaData = QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( 1 ).
            addFields( fields ).
            build();

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        if ( searchResult.isEmpty() )
        {
            return null;
        }

        return searchResult.getResults().getFirstHit();
    }


    /**
     * Fetch blobKeys for provided Ids.
     * The order of the provided Ids are maintained in result.
     *
     * @param query
     * @return
     */
    @Override
    public NodeVersionIds getByVersionIds( final WorkspaceIdsQuery query )
    {
        final Set<String> entityIdsAsStrings = query.getEntityIdsAsStrings();

        return doGetByIds( query.getWorkspace(), entityIdsAsStrings );
    }

    private NodeVersionIds doGetByIds( final Workspace workspace, final Set<String> entityIdsAsStrings )
    {
        final String workspaceName = workspace.getName();

        final TermsQueryBuilder idsQuery =
            new TermsQueryBuilder( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME, entityIdsAsStrings );
        final BoolQueryBuilder boolQueryBuilder = joinWithWorkspaceQuery( workspaceName, idsQuery );
        final QueryMetaData queryMetaData = createGetBlobKeyQueryMetaData( entityIdsAsStrings.size() );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, boolQueryBuilder );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Map<String, SearchResultField> orderedResultMap =
            getSearchResultFieldsWithPreservedOrder( workspace, entityIdsAsStrings, searchResult );

        return fieldValuesToVersionIds( orderedResultMap.values() );
    }

    private Map<String, SearchResultField> getSearchResultFieldsWithPreservedOrder( final Workspace workspace,
                                                                                    final Set<String> entityIdsAsStrings,
                                                                                    final SearchResult searchResult )
    {
        return Maps.asMap( entityIdsAsStrings,
                           new EntityIdToSearchResultFieldMapper( searchResult, WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME,
                                                                  workspace ) );
    }


    @Override
    public NodeVersionId getByPath( final WorkspacePathQuery query )
    {
        final TermQueryBuilder parentQuery =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.PATH_FIELD_NAME, query.getNodePathAsString() );
        final BoolQueryBuilder workspacedByPathQuery = joinWithWorkspaceQuery( query.getWorkspace().getName(), parentQuery );

        final QueryMetaData queryMetaData = createGetBlobKeyQueryMetaData( 1 );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, workspacedByPathQuery );

        if ( searchResult.isEmpty() )
        {
            return null;
        }

        final SearchResultEntry firstHit = searchResult.getResults().getFirstHit();

        final Object value = firstHit.getField( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME ).getValue();

        if ( value == null )
        {
            throw new ElasticsearchDataException(
                "Field " + WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME + " not found on node with path " +
                                                      query.getNodePathAsString() +
                                                      " in workspace " + query.getWorkspace() );
        }

        return NodeVersionId.from( value.toString() );
    }

    @Override
    public NodeVersionIds getByPaths( final WorkspacePathsQuery query )
    {
        final TermsQueryBuilder parentQuery =
            new TermsQueryBuilder( WorkspaceXContentBuilderFactory.PATH_FIELD_NAME, query.getNodePathsAsStrings() );
        final BoolQueryBuilder workspacedByPathsQuery = joinWithWorkspaceQuery( query.getWorkspace().getName(), parentQuery );
        final QueryMetaData queryMetaData = createGetBlobKeyQueryMetaData( query.getNodePathsAsStrings().size() );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, workspacedByPathsQuery );

        final Set<SearchResultField> fieldValues =
            searchResult.getResults().getFields( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public NodeVersionIds findByParent( final WorkspaceParentQuery query )
    {
        final TermQueryBuilder parentQuery =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.PARENT_PATH_FIELD_NAME, query.getParentPath() );
        final BoolQueryBuilder byParentQuery = joinWithWorkspaceQuery( query.getWorkspace().getName(), parentQuery );

        final QueryMetaData queryMetaData = createGetBlobKeyQueryMetaData( DEFAULT_UNKNOWN_SIZE );

        final SearchResult searchResult = elasticsearchDao.get( queryMetaData, byParentQuery );

        if ( searchResult.getResults().getSize() == 0 )
        {
            return NodeVersionIds.empty();
        }

        final Set<SearchResultField> fieldValues =
            searchResult.getResults().getFields( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public EntityIds getEntriesWithDiff( final CompareWorkspacesQuery workspaceDiffQuery )
    {
        final TermQueryBuilder inSource =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.WORKSPACE_FIELD_NAME, workspaceDiffQuery.getSource().getName() );
        final TermQueryBuilder inTarget =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.WORKSPACE_FIELD_NAME, workspaceDiffQuery.getTarget().getName() );

        final long inSourceCount = elasticsearchDao.count( createGetBlobKeyQueryMetaData( 0 ), inSource );
        final long inTargetCount = elasticsearchDao.count( createGetBlobKeyQueryMetaData( 0 ), inTarget );

        final long totalCount = inSourceCount + inTargetCount;

        final BoolQueryBuilder inOnOfTheWorkspaces = new BoolQueryBuilder().
            should( inSource ).
            should( inTarget ).
            minimumNumberShouldMatch( 1 );

        final String changedAggregationName = "changed";

        final TermsBuilder changedAggregationQuery = AggregationBuilders.
            terms( changedAggregationName ).
            size( (int) (long) totalCount ).
            order( Terms.Order.count( true ) );

        final ElasticsearchQuery query = ElasticsearchQuery.newQuery().
            query( inOnOfTheWorkspaces ).
            setAggregations( Sets.newHashSet( changedAggregationQuery ) ).
            size( 0 ).
            from( 0 ).
            index( WORKSPACE_INDEX.getName() ).
            indexType( IndexType.NODE ).
            build();

        final SearchResult searchResult = elasticsearchDao.search( query );

        final Aggregation changedAggregation = searchResult.getAggregations().get( changedAggregationName );

        if ( changedAggregation instanceof BucketAggregation )
        {
            return ChangedIdsResolver.resolve( (BucketAggregation) changedAggregation );
        }
        else
        {
            throw new ClassCastException(
                "Aggregation of unexpected type, should be BucketAggregation, was " + changedAggregation.getClass().getName() );
        }
    }

    @Override
    public boolean hasChildren( final NodePath parent, final Workspace workspace )
    {
        final QueryMetaData queryMetaData = QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( 0 ).
            build();

        final TermQueryBuilder findWithParentQuery =
            new TermQueryBuilder( WorkspaceXContentBuilderFactory.PARENT_PATH_FIELD_NAME, parent.toString() );
        final BoolQueryBuilder query = joinWithWorkspaceQuery( workspace.getName(), findWithParentQuery );

        final long count = elasticsearchDao.count( queryMetaData, query );

        return count > 0;
    }

    private NodeVersionIds fieldValuesToVersionIds( final Collection<SearchResultField> fieldValues )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        for ( final SearchResultField searchResultField : fieldValues )
        {
            if ( searchResultField == null )
            {
                continue;
            }

            builder.add( NodeVersionId.from( searchResultField.getValue().toString() ) );
        }
        return builder.build();
    }

    private QueryMetaData createGetBlobKeyQueryMetaData( final int numberOfHits )
    {
        final SortBuilder fieldSortBuilder = new FieldSortBuilder( BUILTIN_TIMESTAMP_FIELD ).order( SortOrder.DESC );

        return QueryMetaData.create( WORKSPACE_INDEX ).
            indexType( IndexType.NODE ).
            from( 0 ).
            size( numberOfHits ).
            addField( WorkspaceXContentBuilderFactory.ENTITY_ID_FIELD_NAME ).
            addField( WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME ).
            addSort( fieldSortBuilder ).
            build();
    }

    private BoolQueryBuilder joinWithWorkspaceQuery( final String workspaceName, final QueryBuilder specificQuery )
    {
        final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        final TermQueryBuilder workspaceQuery = new TermQueryBuilder( WorkspaceXContentBuilderFactory.WORKSPACE_FIELD_NAME, workspaceName );
        boolQueryBuilder.must( specificQuery );
        boolQueryBuilder.must( workspaceQuery );

        return boolQueryBuilder;
    }

    private final class EntityIdToSearchResultFieldMapper
        implements com.google.common.base.Function<String, SearchResultField>
    {
        private final SearchResult searchResult;

        private final String fieldName;

        private final Workspace workspace;

        private EntityIdToSearchResultFieldMapper( final SearchResult searchResult, final String fieldName, final Workspace workspace )
        {
            this.searchResult = searchResult;
            this.fieldName = fieldName;
            this.workspace = workspace;
        }

        @Override
        public SearchResultField apply( final String entityId )
        {
            final WorkspaceDocumentId workspaceDocumentId = new WorkspaceDocumentId( EntityId.from( entityId ), this.workspace );

            final SearchResultEntry entry = this.searchResult.getEntry( workspaceDocumentId.toString() );
            return entry != null ? entry.getField( fieldName ) : null;
        }
    }

    @Inject
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}

