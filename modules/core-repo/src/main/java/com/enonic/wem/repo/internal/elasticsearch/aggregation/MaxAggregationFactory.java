package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.max.Max;

import com.enonic.xp.core.aggregation.SingleValueMetricAggregation;

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
