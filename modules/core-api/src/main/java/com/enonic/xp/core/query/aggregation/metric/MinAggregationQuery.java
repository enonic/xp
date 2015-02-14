package com.enonic.xp.core.query.aggregation.metric;

import com.enonic.xp.core.query.aggregation.MetricAggregationQuery;

public class MinAggregationQuery
    extends MetricAggregationQuery
{
    private MinAggregationQuery( final Builder builder )
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

        public MinAggregationQuery build()
        {
            return new MinAggregationQuery( this );
        }
    }
}
