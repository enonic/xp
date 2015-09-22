package com.enonic.wem.repo.internal.elasticsearch.search;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQueryTranslator;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.AclFilterBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.search.SearchDao;
import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.result.ReturnValue;
import com.enonic.wem.repo.internal.storage.result.SearchHits;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.wem.repo.internal.version.NodeVersionQuery;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.expr.OrderExpressions;
import com.enonic.xp.query.filter.ValueFilter;

@Component
public class ElasticsearchSearchDao
    implements SearchDao
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public SearchResult find( final NodeVersionQuery query, final InternalContext context )
    {
        return null;
    }

    @Override
    public SearchResult search( final SearchRequest searchRequest )
    {
        final ElasticsearchQuery esQuery = ElasticsearchQueryTranslator.translate( searchRequest );

        if ( searchRequest.getQuery().getSearchMode().equals( SearchMode.COUNT ) )
        {
            final long count = elasticsearchDao.count( esQuery );

            return SearchResult.create().
                hits( SearchHits.create().
                    totalHits( count ).
                    build() ).
                build();
        }

        System.out.println( esQuery );

        return this.elasticsearchDao.find( esQuery );
    }

    @Override
    public NodeVersionDiffResult versionDiff( final NodeVersionDiffQuery query, final InternalContext context )
    {
        return null;
    }

    private NodeVersionIds doFindByIds( final NodeIds nodeIds, final OrderExpressions orderExprs, final InternalContext context )
    {
        if ( nodeIds.isEmpty() )
        {
            return NodeVersionIds.empty();
        }

        final Branch branch = context.getBranch();

        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( AclFilterBuilderFactory.create( context.getPrincipalsKeys() ) ).
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.ID.getPath() ).
                addValues( nodeIds.getAsStrings() ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( context.getRepositoryId() ) ).
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

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
