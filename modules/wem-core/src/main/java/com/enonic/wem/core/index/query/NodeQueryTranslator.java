package com.enonic.wem.core.index.query;

import org.elasticsearch.index.query.QueryBuilder;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.api.entity.query.NodeQuery;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.core.elasticsearch.ElasticsearchQuery;
import com.enonic.wem.core.entity.index.NodeIndexDocumentFactory;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.index.query.aggregation.AggregationBuilderFactory;
import com.enonic.wem.core.index.query.builder.FilterBuilderFactory;
import com.enonic.wem.core.index.query.builder.QueryBuilderFactory;
import com.enonic.wem.core.index.query.builder.SortBuilderFactory;

public class NodeQueryTranslator
    extends EntityQueryTranslator
{

    public static ElasticsearchQuery translate( final NodeQuery nodeQuery, final Workspace workspace )
    {
        final QueryBuilder queryWithQueryFilters = createQueryWithQueryFilters( nodeQuery );

        final ElasticsearchQuery.Builder queryBuilder = ElasticsearchQuery.newQuery().
            index( workspace.getSearchIndexName() ).
            indexType( IndexType.NODE ).
            query( queryWithQueryFilters ).
            setAggregations( AggregationBuilderFactory.create( nodeQuery.getAggregationQueries() ) ).
            sortBuilders( SortBuilderFactory.create( nodeQuery.getOrderBys() ) ).
            filter( FilterBuilderFactory.create( nodeQuery.getPostFilters() ) ).
            from( nodeQuery.getFrom() ).
            size( nodeQuery.getSize() );

        return queryBuilder.build();
    }

    private static QueryBuilder createQueryWithQueryFilters( final NodeQuery nodeQuery )
    {
        final QueryBuilderFactory.Builder queryBuilderBuilder = QueryBuilderFactory.create().queryExpr( nodeQuery.getQuery() ).
            addQueryFilters( nodeQuery.getQueryFilters() );

        if ( nodeQuery.getParent() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexDocumentFactory.PARENT_PATH_KEY ).
                addValue( Value.newString( nodeQuery.getParent().toString() ) ).
                setCache( true ).
                build() );
        }

        if ( nodeQuery.getPath() != null )
        {
            queryBuilderBuilder.addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexDocumentFactory.PATH_KEY ).
                addValue( Value.newString( nodeQuery.getPath().toString() ) ).
                build() );
        }

        return queryBuilderBuilder.build();
    }


}
