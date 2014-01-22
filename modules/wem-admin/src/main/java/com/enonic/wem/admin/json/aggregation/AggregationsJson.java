package com.enonic.wem.admin.json.aggregation;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.query.aggregation.Aggregation;
import com.enonic.wem.api.query.aggregation.Aggregations;

public class AggregationsJson
{
    final ImmutableSet<AggregationJson> aggregations;

    public AggregationsJson( final Aggregations aggregations )
    {
        ImmutableSet.Builder<AggregationJson> builder = ImmutableSet.builder();

        for ( final Aggregation aggregation : aggregations )
        {
            builder.add( new AggregationJson( aggregation ) );
        }

        this.aggregations = builder.build();
    }

    public ImmutableSet<AggregationJson> getAggregations()
    {
        return aggregations;
    }
}

