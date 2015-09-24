package com.enonic.xp.repo.impl.elasticsearch.aggregation.query;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.MaxBuilder;

import com.enonic.xp.query.aggregation.metric.MaxAggregationQuery;
import com.enonic.xp.repo.impl.index.query.IndexQueryFieldNameResolver;

class MaxAggregationQueryBuilderFactory
{
    static AbstractAggregationBuilder create( final MaxAggregationQuery aggregationQuery )
    {
        return new MaxBuilder( aggregationQuery.getName() ).
            field( IndexQueryFieldNameResolver.resolveNumericFieldName( aggregationQuery.getFieldName() ) );
    }

}
