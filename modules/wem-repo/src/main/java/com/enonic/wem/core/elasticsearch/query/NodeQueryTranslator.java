package com.enonic.wem.core.elasticsearch.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.core.elasticsearch.aggregation.AggregationBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.FilterBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.core.elasticsearch.query.builder.SortQueryBuilderFactory;
import com.enonic.wem.core.entity.query.NodeQuery;
import com.enonic.wem.core.index.IndexContext;
import com.enonic.wem.core.repository.IndexNameResolver;

public class NodeQueryTranslator
{

    public static ElasticsearchQuery translate( final NodeQuery nodeQuery, final IndexContext indexContext )
    {
        final QueryBuilder queryWithQueryFilters = createQueryWithQueryFilters( nodeQuery );

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

    private static QueryBuilder createQueryWithQueryFilters( final NodeQuery nodeQuery )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.create().
            queryExpr( nodeQuery.getQuery() ).
            addQueryFilters( nodeQuery.getQueryFilters() );

        addAclFilter( nodeQuery, queryBuilderBuilder );
        addParentFilter( nodeQuery, queryBuilderBuilder );
        addPathFilter( nodeQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build();
    }

    private static void addAclFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getPrincipals() != null && nodeQuery.getPrincipals().isNotEmpty() )
        {
            final ValueFilter.Builder valueBuilder = ValueFilter.create().
                fieldName( IndexPaths.HAS_READ_KEY );

            for ( final Principal principal : nodeQuery.getPrincipals() )
            {
                valueBuilder.addValue( Value.newString( principal ) );
            }

            queryBuilderBuilder.addQueryFilter( valueBuilder.build() );
        }
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
