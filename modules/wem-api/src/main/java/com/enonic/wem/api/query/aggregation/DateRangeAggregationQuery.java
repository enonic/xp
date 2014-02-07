package com.enonic.wem.api.query.aggregation;

public class DateRangeAggregationQuery
    extends RangeAggregationQuery<DateRange>
{
    public DateRangeAggregationQuery( final RangeAggregationQuery.Builder builder )
    {
        super( builder );
    }

    public static class Builder
        extends RangeAggregationQuery.Builder<Builder, DateRange>
    {
        public Builder( final String name )
        {
            super( name );
        }

        public DateRangeAggregationQuery build()
        {
            return new DateRangeAggregationQuery( this );
        }

    }
}
