package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.max.Max;

import com.enonic.xp.aggregation.SingleValueMetricAggregation;

class MaxAggregationFactory
    extends AggregationsFactory
{
    static SingleValueMetricAggregation create( final Max value )
    {
        return SingleValueMetricAggregation.create( value.getName() ).
            value( value.getValue() ).
            build();
    }
}
