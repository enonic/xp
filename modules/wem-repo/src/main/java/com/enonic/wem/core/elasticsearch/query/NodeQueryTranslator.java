package com.enonic.wem.core.elasticsearch.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.core.elasticsearch.aggregation.AggregationBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.AclFilterBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.FilterBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.repository.IndexNameResolver;
import com.enonic.wem.repo.NodeQuery;

public class NodeQueryTranslator
{

    public static ElasticsearchQuery translate( final NodeQuery nodeQuery, final IndexContext indexContext )
    {
        final QueryBuilder queryWithQueryFilters = createQueryWithQueryFilters( nodeQuery, indexContext );

        final ElasticsearchQuery.Builder queryBuilder = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveSearchIndexName( indexContext.getRepositoryId() ) ).
            indexType( indexContext.getWorkspace().getName() ).
            query( queryWithQueryFilters ).
            setAggregations( AggregationBuilderFactory.create( nodeQuery.getAggregationQueries() ) ).
            sortBuilders( SortQueryBuilderFactory.create( nodeQuery.getOrderBys() ) ).
            filter( FilterBuilderFactory.create( nodeQuery.getPostFilters() ) ).
            from( nodeQuery.getFrom() ).
            size( nodeQuery.getSize() );

        return queryBuilder.build();
    }

    private static QueryBuilder createQueryWithQueryFilters( final NodeQuery nodeQuery, final IndexContext context )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.create().
            queryExpr( nodeQuery.getQuery() ).
            addQueryFilters( nodeQuery.getQueryFilters() );

        addAclFilter( queryBuilderBuilder, context.getPrincipals() );
        addParentFilter( nodeQuery, queryBuilderBuilder );
        addPathFilter( nodeQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build();
    }

    private static void addAclFilter( final QueryBuilderFactory.Builder queryBuilderBuilder, final Principals principals )
    {
        queryBuilderBuilder.addQueryFilter( AclFilterBuilderFactory.create( principals ) );
    }

    private static void addPathFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getPath() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( IndexPaths.PATH_KEY ).
                addValue( Value.newString( nodeQuery.getPath().toString() ) ).
                build() );
        }
    }

    private static void addParentFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getParent() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( IndexPaths.PARENT_PATH_KEY ).
                addValue( Value.newString( nodeQuery.getParent().toString() ) ).
                build() );
        }
    }


}
