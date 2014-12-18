package com.enonic.wem.repo.internal.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;

import com.enonic.wem.api.aggregation.SingleValueMetricAggregation;

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
