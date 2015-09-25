package com.enonic.wem.repo.internal.elasticsearch.query.translator;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.repo.internal.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.FilterBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.elasticsearch.query.translator.builder.SortQueryBuilderFactory;
import com.enonic.wem.repo.internal.search.SearchRequest;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchQuery;
import com.enonic.wem.repo.internal.version.VersionIndexPath;

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
            setReturnFields( ReturnFields.from( VersionIndexPath.NODE_ID, VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP ) ).
            setAggregations( aggregationsBuilder.create( query.getAggregationQueries() ) ).
            sortBuilders( sortBuilder.create( query.getOrderBys() ) ).
            filter( filterBuilderFactory.create( query.getPostFilters() ) ).
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
