package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.NodeQueryTranslator;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.AclFilterBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.index.query.QueryResultFactory;
import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.result.ReturnValue;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.filter.ValueFilter;

@Component
public class ElasticsearchQueryService
    implements QueryService
{
    private final QueryResultFactory queryResultFactory = new QueryResultFactory();

    private ElasticsearchDao elasticsearchDao;

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

    @Override
    public NodeVersionIds find( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
    {
        return doFindByIds( nodeIds, orderExprs, indexContext );
    }

    private NodeVersionIds doFindByIds( final NodeIds nodeIds, final OrderExpressions orderExprs, final IndexContext indexContext )
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

        final Set<ReturnValue> fieldValues = searchResult.getResults().getFields( NodeIndexPath.VERSION.getPath() );

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
                addValue( ValueFactory.newString( parentPath.toString() ) ).
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

    private NodeVersionIds fieldValuesToVersionIds( final Collection<ReturnValue> fieldValues )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();

        for ( final ReturnValue returnValue : fieldValues )
        {
            if ( returnValue == null )
            {
                continue;
            }

            builder.add( NodeVersionId.from( returnValue.getSingleValue().toString() ) );
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
