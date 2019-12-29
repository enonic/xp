package com.enonic.xp.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class SingleValueMetricAggregation
    extends MetricAggregation
{
    private final double value;

    private SingleValueMetricAggregation( final Builder builder )
    {
        super( builder );
        this.value = builder.value;
    }

    public double getValue()
    {
        return value;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static final class Builder
        extends Aggregation.Builder<Builder>
    {
        private double value;


        private Builder( final String name )
        {
            super( name );
        }

        public Builder value( double value )
        {
            this.value = value;
            return this;
        }

        public SingleValueMetricAggregation build()
        {
            return new SingleValueMetricAggregation( this );
        }
    }

}
