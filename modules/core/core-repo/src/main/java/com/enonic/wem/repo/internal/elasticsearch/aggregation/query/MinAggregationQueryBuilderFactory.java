package com.enonic.wem.repo.internal.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.MinBuilder;

import com.enonic.wem.repo.internal.index.query.IndexQueryFieldNameResolver;
import com.enonic.xp.query.aggregation.metric.MinAggregationQuery;

class MinAggregationQueryBuilderFactory
{
    static AbstractAggregationBuilder create( final MinAggregationQuery aggregationQuery )
    {
        return new MinBuilder( aggregationQuery.getName() ).
            field( IndexQueryFieldNameResolver.resolveNumericFieldName( aggregationQuery.getFieldName() ) );
    }
}
