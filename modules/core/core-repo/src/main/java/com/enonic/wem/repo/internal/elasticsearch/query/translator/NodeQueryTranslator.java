package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.repo.internal.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.PrincipalKeys;

class NodeQueryTranslator
    extends AbstractElasticsearchQueryTranslator
{
    static ElasticsearchQuery translate( final SearchRequest request )
    {
        final NodeQuery query = (NodeQuery) request.getQuery();

        final QueryBuilder queryBuilder = createQueryWithFilters( query, request.getAcl() );

        final StorageSettings settings = request.getSettings();

        final ElasticsearchQuery.Builder esQuery = ElasticsearchQuery.create().
            index( settings.getStorageName().getName() ).
            indexType( settings.getStorageType().getName() ).
            query( queryBuilder ).
            setAggregations( AggregationQueryBuilderFactory.create( query.getAggregationQueries() ) ).
            sortBuilders( SortQueryBuilderFactory.create( query.getOrderBys() ) ).
            filter( FilterBuilderFactory.create( query.getPostFilters() ) ).
            setReturnFields( request.getReturnFields() ).
            from( query.getFrom() ).
            size( query.getSize() );

        return esQuery.build();
    }

    private static QueryBuilder createQueryWithFilters( final NodeQuery nodeQuery, final PrincipalKeys acl )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = createQuery( nodeQuery );

        final Filter aclFilter = AclFilterBuilderFactory.create( acl );

        if ( aclFilter != null )
        {
            queryBuilderBuilder.addQueryFilter( aclFilter );
        }

        addParentFilter( nodeQuery, queryBuilderBuilder );
        addPathFilter( nodeQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build();
    }

    private static void addPathFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getPath() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PATH.getPath() ).
                addValue( ValueFactory.newString( nodeQuery.getPath().toString() ) ).
                build() );
        }
    }

    private static void addParentFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getParent() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PARENT_PATH.getPath() ).
                addValue( ValueFactory.newString( nodeQuery.getParent().toString() ) ).
                build() );
        }
    }
}
