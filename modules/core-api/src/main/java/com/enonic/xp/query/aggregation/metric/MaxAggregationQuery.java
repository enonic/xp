package com.enonic.xp.query.aggregation.metric;


import com.google.common.annotations.Beta;

import com.enonic.xp.query.aggregation.MetricAggregationQuery;

@Beta
public class MaxAggregationQuery
    extends MetricAggregationQuery
{
    private MaxAggregationQuery( final Builder builder )
    {
        super( builder );
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends MetricAggregationQuery.Builder<Builder>
    {
        public Builder( final String name )
        {
            super( name );
        }

        public MaxAggregationQuery build()
        {
            return new MaxAggregationQuery( this );
        }
    }
}