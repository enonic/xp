package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.Value;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.query.QueryException;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.NodeQueryTranslator;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.AclFilterBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.index.query.QueryResultFactory;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;

@Component
public class ElasticsearchQueryService
    implements QueryService
{
    private ElasticsearchDao elasticsearchDao;

    private final QueryResultFactory queryResultFactory = new QueryResultFactory();

    @Override
    public NodeQueryResult find( final NodeQuery query, final IndexContext context )
    {
        final ElasticsearchQuery esQuery = NodeQueryTranslator.translate( query, context );

        if ( query.isCountOnly() )
        {
            final long count = elasticsearchDao.count( esQuery );

            return NodeQueryResult.create().
                totalHits( count ).
                build();
        }

        //System.out.println( esQuery );

        return doFind( esQuery );
    }

    private NodeQueryResult doFind( final ElasticsearchQuery query )
    {
        final SearchResult searchResult = elasticsearchDao.find( query );

        return translateResult( searchResult );
    }

    public NodeVersionId get( final NodeId nodeId, final IndexContext indexContext )
    {
        final GetResult result = elasticsearchDao.get( GetQuery.create().
            indexName( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexTypeName( indexContext.getBranch().getName() ).
            returnFields( ReturnFields.from( NodeIndexPath.VERSION, NodeIndexPath.PERMISSIONS_READ ) ).
            id( nodeId.toString() ).
            build() );

        if ( result.isEmpty() )
        {
            return null;
        }

        if ( !GetResultCanReadResolver.canRead( indexContext.getPrincipalKeys(), result ) )
        {
            return null;
        }

        final SearchResultFieldValue nodeVersionId = result.getSearchResult().getField( NodeIndexPath.VERSION.getPath() );

        if ( nodeVersionId == null )
        {
            throw new QueryException(
                "Expected field " + NodeIndexPath.VERSION.getPath() + " not found in search result for nodeId " + nodeId );
        }

        return NodeVersionId.from( nodeVersionId.getValue().toString() );
    }


    @Override
    public NodeVersionId get( final NodePath nodePath, final IndexContext indexContext )
    {
        final Branch branch = indexContext.getBranch();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PATH.getPath() ).
                addValue( Value.newString( nodePath.toString() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( branch.getName() ).
            query( queryBuilder ).
            size( 1 ).
            setReturnFields( ReturnFields.from( NodeIndexPath.VERSION ) ).
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

        final SearchResultFieldValue versionKeyField = firstHit.getField( NodeIndexPath.VERSION.getPath() );

        if ( versionKeyField == null )
        {
            throw new ElasticsearchDataException( "Field " + NodeIndexPath.VERSION.getPath() + " not found on node with path " +
                                                      nodePath + " in branch " + branch );
        }

        return NodeVersionId.from( versionKeyField.getValue().toString() );
    }

    @Override
    public NodeVersionIds find( final NodePaths nodePaths, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        if ( nodePaths.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Branch branch = indexContext.getBranch();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PATH.getPath() ).
                addValues( nodePaths.getAsStrings() ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( branch.getName() ).
            query( queryBuilder ).
            sortBuilders( SortQueryBuilderFactory.create( orderExprs ) ).
            setReturnFields( ReturnFields.from( NodeIndexPath.VERSION ) ).
            size( nodePaths.getSize() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Set<SearchResultFieldValue> fieldValues = searchResult.getResults().getFields( NodeIndexPath.VERSION.getPath() );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        return doGetByIds( nodeIds, orderExprs, indexContext );
    }

    private NodeVersionIds doGetByIds( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        if ( nodeIds.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Branch branch = indexContext.getBranch();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.ID.getPath() ).
                addValues( nodeIds.getAsStrings() ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( branch.getName() ).
            query( queryBuilder ).
            sortBuilders( SortQueryBuilderFactory.create( orderExprs ) ).
            setReturnFields( ReturnFields.from( NodeIndexPath.VERSION ) ).
            size( nodeIds.getSize() ).
            build();

        final SearchResult searchResult = elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            NodeVersionIds.empty();
        }

        final Set<SearchResultFieldValue> fieldValues = searchResult.getResults().getFields( NodeIndexPath.VERSION.getPath() );

        return fieldValuesToVersionIds( fieldValues );
    }

    @Override
    public boolean hasChildren( final NodePath parentPath, final IndexContext indexContext )
    {
        final Context context = ContextAccessor.current();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( indexContext.getPrincipalKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PARENT_PATH.getPath() ).
                addValue( Value.newString( parentPath.toString() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() ) ).
            indexType( context.getBranch().getName() ).
            query( queryBuilder ).
            build();

        final long count = elasticsearchDao.count( query );

        return count > 0;
    }

    private NodeVersionIds fieldValuesToVersionIds( final Collection<SearchResultFieldValue> fieldValues )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        for ( final SearchResultFieldValue searchResultFieldValue : fieldValues )
        {
            if ( searchResultFieldValue == null )
            {
                continue;
            }

            builder.add( NodeVersionId.from( searchResultFieldValue.getValue().toString() ) );
        }
        return builder.build();
    }


    private NodeQueryResult translateResult( final SearchResult searchResult )
    {
        return queryResultFactory.create( searchResult );
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
