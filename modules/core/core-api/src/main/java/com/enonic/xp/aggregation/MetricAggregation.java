package com.enonic.xp.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class MetricAggregation
    extends Aggregation
{
    public MetricAggregation( final Builder builder )
    {
        super( builder );
    }
}
