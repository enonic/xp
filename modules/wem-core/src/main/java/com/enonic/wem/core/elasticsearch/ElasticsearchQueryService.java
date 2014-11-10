package com.enonic.wem.core.elasticsearch;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

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
        final SearchResult searchResult = elasticsearchDao.search( query );

        return translateResult( searchResult );
    }

    @Override
    public NodeVersionId get( final NodeId nodeId, final IndexContext indexContext )
    {
        // TODO: Add access-control
        final GetResult result =
            elasticsearchDao.get( QueryProperties.create( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
                indexTypeName( indexContext.getWorkspace().getName() ).
                from( 0 ).
                size( 1 ).
                addField( IndexPaths.VERSION_KEY ).
                build(), nodeId.toString() );

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

        final QueryProperties queryProperties =
            QueryProperties.create( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
                indexTypeName( workspace.getName() ).
                from( 0 ).
                size( 1 ).
                addField( IndexPaths.VERSION_KEY ).
                build();

        // TODO: Add access-control
        final TermQueryBuilder pathQuery = new TermQueryBuilder( IndexPaths.PATH_KEY, nodePath.toString() );

        final SearchResult searchResult = elasticsearchDao.search( queryProperties, pathQuery );

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

        final QueryProperties queryProperties =
            QueryProperties.create( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
                indexTypeName( workspace.getName() ).
                from( 0 ).
                size( nodePaths.getSize() ).
                addField( IndexPaths.VERSION_KEY ).
                setSort( SortQueryBuilderFactory.create( orderExprs ) ).
                build();

        // TODO: Add access-control
        final TermsQueryBuilder pathsQuery = new TermsQueryBuilder( IndexPaths.PATH_KEY, nodePaths.getAsStrings() );

        final SearchResult searchResult = elasticsearchDao.search( queryProperties, pathsQuery );

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

        final QueryProperties queryProperties =
            QueryProperties.create( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
                indexTypeName( workspace.getName() ).
                from( 0 ).
                size( nodeIds.getSize() ).
                addField( IndexPaths.VERSION_KEY ).
                setSort( SortQueryBuilderFactory.create( orderExprs ) ).
                build();

        // TODO: Add access-control
        final TermsQueryBuilder pathsQuery = new TermsQueryBuilder( IndexPaths.ID_KEY, nodeIds.getAsStrings() );

        final SearchResult searchResult = elasticsearchDao.search( queryProperties, pathsQuery );

        if ( searchResult.isEmpty() )
        {
            NodeVersionIds.empty();
        }

        final Set<SearchResultField> fieldValues = searchResult.getResults().getFields( IndexPaths.VERSION_KEY );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public NodeVersionIds getByParent( final NodePath parentPath, final IndexContext indexContext )
    {
        final Workspace workspace = indexContext.getWorkspace();

        final QueryProperties queryProperties =
            QueryProperties.create( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
                indexTypeName( workspace.getName() ).
                from( 0 ).
                size( QueryService.GET_ALL_SIZE_FLAG ).
                addField( IndexPaths.VERSION_KEY ).
                build();

        final TermsQueryBuilder childrenQuery = new TermsQueryBuilder( IndexPaths.PARENT_PATH_KEY, parentPath );

        final SearchResult searchResult = elasticsearchDao.search( queryProperties, childrenQuery );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Set<SearchResultField> fieldValues = searchResult.getResults().getFields( IndexPaths.VERSION_KEY );

        return fieldValuesToVersionIds( fieldValues );
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
