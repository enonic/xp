package com.enonic.xp.aggregation;

import com.google.common.annotations.Beta;

@Beta
public class CardinalityAggregation
    extends MetricAggregation
{
    private final double value;

    private CardinalityAggregation( final Builder builder )
    {
        super( builder );
        this.value = builder.value;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public double getValue()
    {
        return value;
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

        public CardinalityAggregation build()
        {
            return new CardinalityAggregation( this );
        }
    }

}
