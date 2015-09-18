package com.enonic.xp.query.aggregation;

import com.google.common.annotations.Beta;

@Beta
public class DateRangeAggregationQuery
    extends AbstractRangeAggregationQuery<DateRange>
{
    private final String format;

    private DateRangeAggregationQuery( final Builder builder )
    {
        super( builder, builder.ranges );
        this.format = builder.format;
    }

    public String getFormat()
    {
        return format;
    }

    public static Builder create( final String name )
    {
        return new Builder( name );
    }

    public static class Builder
        extends AbstractRangeAggregationQuery.Builder<Builder, DateRange>
    {
        private String format;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder format( final String format )
        {
            this.format = format;
            return this;
        }

        public DateRangeAggregationQuery build()
        {
            return new DateRangeAggregationQuery( this );
        }
    }
}
