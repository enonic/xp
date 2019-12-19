package com.enonic.xp.repo.impl.elasticsearch.aggregation;

import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;

import com.enonic.xp.aggregation.CardinalityAggregation;

class CardinalityAggregationFactory
    extends AggregationsFactory
{
    static CardinalityAggregation create( final ParsedCardinality value )
    {
        return CardinalityAggregation.create( value.getName() ).
            value( value.getValue() ).
            build();
    }
}
