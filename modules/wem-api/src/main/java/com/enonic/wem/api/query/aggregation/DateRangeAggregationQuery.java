package com.enonic.wem.api.query.aggregation;

public class DateRangeAggregationQuery
    extends AbstractRangeAggregationQuery<DateRange>
{
    private DateRangeAggregationQuery( final Builder builder )
    {
        super( builder, builder.ranges );
    }

    public static class Builder
        extends AbstractRangeAggregationQuery.Builder<Builder, DateRange>
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
