package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.FilterBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.QueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.builder.SortQueryBuilderFactory;
import com.enonic.xp.repo.impl.search.SearchRequest;

public class NodeBranchQueryTranslator
{
    private final QueryFieldNameResolver fieldNameResolver = new StoreQueryFieldNameResolver();

    private final SortQueryBuilderFactory sortBuilder = new SortQueryBuilderFactory( fieldNameResolver );

    private final FilterBuilderFactory filterBuilderFactory = new FilterBuilderFactory( fieldNameResolver );

    private final AggregationQueryBuilderFactory aggregationsBuilder = new AggregationQueryBuilderFactory( fieldNameResolver );


    public ElasticsearchQuery translate( final SearchRequest request )
    {
        final NodeBranchQuery query = (NodeBranchQuery) request.getQuery();

        final QueryBuilder queryBuilder = createQueryBuilder( query );

        return ElasticsearchQuery.create().
            index( request.getSettings().getStorageName().getName() ).
            indexType( request.getSettings().getStorageType().getName() ).
            query( queryBuilder ).
            setReturnFields(
                ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.TIMESTAMP, BranchIndexPath.PATH,
                                   BranchIndexPath.STATE ) ).
            setAggregations( aggregationsBuilder.create( query.getAggregationQueries() ) ).
            sortBuilders( sortBuilder.create( query.getOrderBys() ) ).
            filter( filterBuilderFactory.create( query.getPostFilters() ) ).
            searchType( SearchType.valueOf( request.getSearchType().toString() ) ).
            size( query.getSize() ).
            from( query.getFrom() ).
            build();
    }


    private QueryBuilder createQueryBuilder( final NodeBranchQuery nodeBranchQuery )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.newBuilder().
            queryExpr( nodeBranchQuery.getQuery() ).
            addQueryFilters( nodeBranchQuery.getQueryFilters() ).
            fieldNameResolver( this.fieldNameResolver );
        return queryBuilderBuilder.build().create();
    }

}
