package com.enonic.xp.aggregation;

import com.google.common.annotations.Beta;

@Beta
public abstract class MetricAggregation
    extends Aggregation
{
    public MetricAggregation( final Builder builder )
    {
        super( builder );
    }
}
