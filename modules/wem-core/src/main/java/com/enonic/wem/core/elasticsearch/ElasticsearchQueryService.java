package com.enonic.wem.core.elasticsearch;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.query.QueryException;
import com.enonic.wem.api.query.expr.OrderExpressions;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.core.elasticsearch.query.NodeQueryTranslator;
import com.enonic.wem.core.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodePaths;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.index.query.NodeQueryResult;
import com.enonic.wem.core.index.query.QueryResultFactory;
import com.enonic.wem.core.index.query.QueryService;
import com.enonic.wem.core.index.result.GetResult;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;
import com.enonic.wem.core.repository.IndexNameResolver;

public class ElasticsearchQueryService
    implements QueryService
{
    private ElasticsearchDao elasticsearchDao;

    private final QueryResultFactory queryResultFactory = new QueryResultFactory();


    @Override
    public NodeQueryResult find( final NodeQuery query, final IndexContext context )
    {
        return doFind( NodeQueryTranslator.translate( query, context ) );
    }

    private NodeQueryResult doFind( final ElasticsearchQuery query )
    {
        final SearchResult searchResult = elasticsearchDao.find( query );

        return translateResult( searchResult );
    }

    @Override
    public NodeVersionId get( final NodeId nodeId, final IndexContext indexContext )
    {
        // TODO: Add access-control
        final GetResult result = elasticsearchDao.get( GetQuery.create().
            indexName( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexTypeName( indexContext.getWorkspace().getName() ).
            returnFields( ReturnFields.from( IndexPaths.VERSION_KEY ) ).
            id( nodeId ).
            build() );

        if ( result.isEmpty() )
        {
            return null;
        }

        final SearchResultField nodeVersionId = result.getResult().getField( IndexPaths.VERSION_KEY );

        if ( nodeVersionId == null )
        {
            throw new QueryException( "Expected field " + IndexPaths.VERSION_KEY + " not found in search result for nodeId " + nodeId );
        }

        return NodeVersionId.from( nodeVersionId.getValue().toString() );
    }

    @Override
    public NodeVersionId get( final NodePath nodePath, final IndexContext indexContext )
    {
        final Workspace workspace = indexContext.getWorkspace();

        final TermQueryBuilder pathQuery = new TermQueryBuilder( IndexPaths.PATH_KEY, nodePath.toString() );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( workspace.getName() ).
            query( pathQuery ).
            size( 1 ).
            setReturnFields( ReturnFields.from( IndexPaths.VERSION_KEY ) ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return null;
        }

        if ( searchResult.getResults().getTotalHits() > 1 )
        {
            throw new QueryException( "Expected at most 1 hit, found " + searchResult.getResults().getTotalHits() );
        }

        final SearchResultEntry firstHit = searchResult.getResults().getFirstHit();

        final SearchResultField versionKeyField = firstHit.getField( IndexPaths.VERSION_KEY );

        if ( versionKeyField == null )
        {
            throw new ElasticsearchDataException( "Field " + IndexPaths.VERSION_KEY + " not found on node with path " +
                                                      nodePath + " in workspace " + workspace );
        }

        return NodeVersionId.from( versionKeyField.getValue().toString() );
    }

    @Override
    public NodeVersionIds find( final NodePaths nodePaths, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        final Workspace workspace = indexContext.getWorkspace();

        final TermsQueryBuilder pathsQuery = new TermsQueryBuilder( IndexPaths.PATH_KEY, nodePaths.getAsStrings() );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( workspace.getName() ).
            query( pathsQuery ).
            sortBuilders( SortQueryBuilderFactory.create( orderExprs ) ).
            setReturnFields( ReturnFields.from( IndexPaths.VERSION_KEY ) ).
            size( nodePaths.getSize() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Set<SearchResultField> fieldValues = searchResult.getResults().getFields( IndexPaths.VERSION_KEY );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        final Workspace workspace = indexContext.getWorkspace();

        final TermsQueryBuilder pathsQuery = new TermsQueryBuilder( IndexPaths.ID_KEY, nodeIds.getAsStrings() );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( workspace.getName() ).
            query( pathsQuery ).
            sortBuilders( SortQueryBuilderFactory.create( orderExprs ) ).
            setReturnFields( ReturnFields.from( IndexPaths.VERSION_KEY ) ).
            size( nodeIds.getSize() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            NodeVersionIds.empty();
        }

        final Set<SearchResultField> fieldValues = searchResult.getResults().getFields( IndexPaths.VERSION_KEY );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public boolean hasChildren( final NodePath parentPath, final IndexContext indexContext )
    {
        final Context context = ContextAccessor.current();

        final TermQueryBuilder findWithParentQuery = new TermQueryBuilder( IndexPaths.PARENT_PATH_KEY, parentPath.toString() );

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() ) ).
            indexType( context.getWorkspace().getName() ).
            query( findWithParentQuery ).
            build();

        final long count = elasticsearchDao.count( query );

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


    private NodeQueryResult translateResult( final SearchResult searchResult )
    {
        return queryResultFactory.create( searchResult );
    }

    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
