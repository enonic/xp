package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.Query;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.AclFilterBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.FilterBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.SortQueryBuilderFactory;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.security.PrincipalKeys;

public class NodeQueryTranslator
{
    private final QueryFieldNameResolver fieldNameResolver = new SearchQueryFieldNameResolver();

    private final SortQueryBuilderFactory sortBuilder = new SortQueryBuilderFactory( fieldNameResolver );

    private final FilterBuilderFactory filterBuilderFactory = new FilterBuilderFactory( fieldNameResolver );

    private final AggregationQueryBuilderFactory aggregationsBuilder = new AggregationQueryBuilderFactory( fieldNameResolver );

    public ElasticsearchQuery translate( final SearchRequest request )
    {
        final NodeQuery query = (NodeQuery) request.getQuery();

        final QueryBuilder queryBuilder = createQueryWithFilters( query, request.getAcl() );

        final StorageSettings settings = request.getSettings();

        final ElasticsearchQuery.Builder esQuery = ElasticsearchQuery.create().
            index( settings.getStorageName().getName() ).
            indexType( settings.getStorageType().getName() ).
            query( queryBuilder ).
            setAggregations( aggregationsBuilder.create( query.getAggregationQueries() ) ).
            sortBuilders( sortBuilder.create( query.getOrderBys() ) ).
            filter( filterBuilderFactory.create( query.getPostFilters() ) ).
            searchType( SearchType.valueOf( request.getSearchType().toString() ) ).
            setReturnFields( request.getReturnFields() ).
            from( query.getFrom() ).
            size( query.getSize() );

        return esQuery.build();
    }

    private QueryBuilder createQueryWithFilters( final NodeQuery nodeQuery, final PrincipalKeys acl )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = createQuery( nodeQuery );

        final Filter aclFilter = AclFilterBuilderFactory.create( acl );

        if ( aclFilter != null )
        {
            queryBuilderBuilder.addQueryFilter( aclFilter );
        }

        addParentFilter( nodeQuery, queryBuilderBuilder );
        addPathFilter( nodeQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build().create();
    }

    private QueryBuilderFactory.Builder createQuery( final Query nodeQuery )
    {
        return QueryBuilderFactory.newBuilder().
            queryExpr( nodeQuery.getQuery() ).
            addQueryFilters( nodeQuery.getQueryFilters() ).
            fieldNameResolver( this.fieldNameResolver );
    }

    private void addPathFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeQuery.getPath() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PATH.getPath() ).
                addValue( ValueFactory.newString( nodeQuery.getPath().toString() ) ).
                build() );
        }
    }

    private void addParentFilter( final NodeQuery nodeQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
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
