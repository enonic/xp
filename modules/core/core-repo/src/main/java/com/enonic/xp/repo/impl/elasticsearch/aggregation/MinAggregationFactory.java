package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.min.Min;

import com.enonic.xp.aggregation.SingleValueMetricAggregation;

class MinAggregationFactory
    extends AggregationsFactory
{
    static SingleValueMetricAggregation create( final Min value )
    {
        return SingleValueMetricAggregation.create( value.getName() ).
            value( value.getValue() ).
            build();
    }
}
