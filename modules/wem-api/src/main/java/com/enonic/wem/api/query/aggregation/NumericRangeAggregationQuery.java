package com.enonic.wem.api.query.aggregation;

public class NumericRangeAggregationQuery
    extends RangeAggregationQuery<NumericRange>
{

    public NumericRangeAggregationQuery( final RangeAggregationQuery.Builder builder )
    {
        super( builder );
    }

    public static class Builder
        extends RangeAggregationQuery.Builder<Builder, NumericRange>
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
