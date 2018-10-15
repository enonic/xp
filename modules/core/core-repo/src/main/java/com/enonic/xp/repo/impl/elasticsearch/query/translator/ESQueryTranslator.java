package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.query.Query;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.elasticsearch.aggregation.query.AggregationQueryBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.source.ESSource;
import com.enonic.xp.repo.impl.elasticsearch.query.source.ESSourceFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.FilterBuilderFactory;
import com.enonic.xp.repo.impl.elasticsearch.query.translator.factory.SortQueryBuilderFactory;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.version.search.NodeVersionDiffQuery;

public class ESQueryTranslator
{
    public static ElasticsearchQuery translate( final SearchRequest request )
    {
        final Query query = request.getQuery();

        if ( query instanceof NodeQuery )
        {
            return doTranslate( request, new NodeQueryTranslator( (NodeQuery) query ) );
        }

        if ( query instanceof NodeVersionQuery )
        {
            return doTranslate( request, new NodeVersionQueryTranslator( (NodeVersionQuery) query ) );
        }

        if ( query instanceof NodeBranchQuery )
        {
            return doTranslate( request, new NodeBranchQueryTranslator( (NodeBranchQuery) query ) );
        }

        if ( query instanceof NodeVersionDiffQuery )
        {
            return doTranslate( request, new NodeVersionDiffQueryTranslator( (NodeVersionDiffQuery) query ) );
        }

        throw new UnsupportedOperationException( "Queries of type " + query.getClass() + " not implemented yes" );
    }

    private static ElasticsearchQuery doTranslate( final SearchRequest request, final QueryTypeTranslator queryTypeTranslator )
    {
        final ESSource esSource = ESSourceFactory.create( request.getSearchSource() );
        final QueryBuilder queryBuilder = queryTypeTranslator.createQueryBuilder( esSource.getFilters() );

        final Query query = request.getQuery();

        final Set<AbstractAggregationBuilder> aggregations =
            new AggregationQueryBuilderFactory( queryTypeTranslator.getFieldNameResolver() ).create( query.getAggregationQueries() );

        final List<SortBuilder> sortBuilders =
            new SortQueryBuilderFactory( queryTypeTranslator.getFieldNameResolver() ).create( query.getOrderBys() );

        final QueryBuilder filterBuilder =
            new FilterBuilderFactory( queryTypeTranslator.getFieldNameResolver() ).create( query.getPostFilters() );

        return ElasticsearchQuery.create().
            addIndexNames( esSource.getIndexNames() ).
            addIndexTypes( esSource.getIndexTypes() ).
            query( queryBuilder ).
            from( query.getFrom() ).
            size( query.getSize() ).
            explain( query.isExplain() ).
            setReturnFields( request.getReturnFields() ).
            setAggregations( aggregations ).
            sortBuilders( sortBuilders ).
            filter( filterBuilder ).
            batchSize( queryTypeTranslator.getBatchSize() ).
            searchMode( query.getSearchMode() ).
            searchOptimizer( queryTypeTranslator.getSearchOptimizer() ).
            build();
    }

}
