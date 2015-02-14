package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.min.Min;

import com.enonic.xp.core.aggregation.SingleValueMetricAggregation;

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
