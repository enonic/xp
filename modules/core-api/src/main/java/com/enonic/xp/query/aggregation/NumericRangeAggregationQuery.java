package com.enonic.xp.query.aggregation;

public class NumericRangeAggregationQuery
    extends AbstractRangeAggregationQuery<NumericRange>
{

    private NumericRangeAggregationQuery( final Builder builder )
    {
        super( builder, builder.ranges );
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends AbstractRangeAggregationQuery.Builder<Builder, NumericRange>
    {

        public Builder( final String name )
        {
            super( name );
        }

        public NumericRangeAggregationQuery build()
        {
            return new NumericRangeAggregationQuery( this );
        }
    }
}
