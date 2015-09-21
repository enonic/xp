package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinBuilder;

import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

class MinAggregationQueryBuilderFactory
{
    static AbstractAggregationBuilder create( final MinAggregationQuery aggregationQuery )
    {
        return new MinBuilder( aggregationQuery.getName() ).
            field( IndexQueryFieldNameResolver.resolveNumericFieldName( aggregationQuery.getFieldName() ) );
    }
}
