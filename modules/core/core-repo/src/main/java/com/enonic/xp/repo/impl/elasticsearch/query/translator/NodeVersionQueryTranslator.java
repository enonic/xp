package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.query.Query;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.FilterBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.SortQueryBuilderFactory;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.version.VersionIndexPath;
import com.enonic.xp.repo.impl.version.search.NodeVersionQuery;

public class NodeVersionQueryTranslator
{
    private final QueryFieldNameResolver fieldNameResolver = new StoreQueryFieldNameResolver();

    private final SortQueryBuilderFactory sortBuilder = new SortQueryBuilderFactory( fieldNameResolver );

    private final FilterBuilderFactory filterBuilderFactory = new FilterBuilderFactory( fieldNameResolver );

    private final AggregationQueryBuilderFactory aggregationsBuilder = new AggregationQueryBuilderFactory( fieldNameResolver );

    public ElasticsearchQuery translate( final SearchRequest searchRequest )
    {
        final NodeVersionQuery nodeVersionQuery = (NodeVersionQuery) searchRequest.getQuery();

        final QueryBuilder queryBuilder = createQueryBuilder( nodeVersionQuery );

        return doCreateEsQuery( nodeVersionQuery, searchRequest, queryBuilder );
    }

    private QueryBuilder createQueryBuilder( final NodeVersionQuery nodeVersionQuery )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.newBuilder().
            queryExpr( nodeVersionQuery.getQuery() ).
            addQueryFilters( nodeVersionQuery.getQueryFilters() ).
            fieldNameResolver( this.fieldNameResolver );

        addNodeIdFilter( nodeVersionQuery, queryBuilderBuilder );

        return queryBuilderBuilder.build().create();
    }

    private ElasticsearchQuery doCreateEsQuery( final Query query, final SearchRequest searchRequest, final QueryBuilder queryWithFilters )
    {
        final StorageSettings settings = searchRequest.getSettings();

        final ElasticsearchQuery.Builder queryBuilder = ElasticsearchQuery.create().
            index( settings.getStorageName().getName() ).
            indexType( settings.getStorageType().getName() ).
            query( queryWithFilters ).
            setAggregations( aggregationsBuilder.create( query.getAggregationQueries() ) ).
            sortBuilders( sortBuilder.create( query.getOrderBys() ) ).
            filter( filterBuilderFactory.create( query.getPostFilters() ) ).
            setReturnFields( searchRequest.getReturnFields() ).
            searchType( SearchType.valueOf( searchRequest.getSearchType().toString() ) ).
            from( query.getFrom() ).
            size( query.getSize() );

        return queryBuilder.build();
    }

    private static void addNodeIdFilter( final NodeVersionQuery nodeVersionQuery, final QueryBuilderFactory.Builder queryBuilderBuilder )
    {
        if ( nodeVersionQuery.getNodeId() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( VersionIndexPath.NODE_ID.getPath() ).
                addValue( ValueFactory.newString( nodeVersionQuery.getNodeId().toString() ) ).
                build() );
        }
    }
}
