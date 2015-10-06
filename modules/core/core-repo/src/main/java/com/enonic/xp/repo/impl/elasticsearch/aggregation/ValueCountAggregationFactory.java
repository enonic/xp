package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;

import com.enonic.xp.aggregation.SingleValueMetricAggregation;

class ValueCountAggregationFactory
    extends AggregationsFactory
{
    static SingleValueMetricAggregation create( final ValueCount value )
    {
        return SingleValueMetricAggregation.create( value.getName() ).
            value( value.getValue() ).
            build();
    }
}
