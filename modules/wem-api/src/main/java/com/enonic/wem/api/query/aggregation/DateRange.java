package com.enonic.wem.api.query.aggregation;

import org.joda.time.DateTime;

public class DateRange
    extends Range
{
    private final DateTime from;

    private final DateTime to;

    public DateRange( final Builder builder )
    {
        super( builder );
        this.from = builder.from;
        this.to = builder.to;
    }

    public DateTime getFrom()
    {
        return from;
    }

    public DateTime getTo()
    {
        return to;
    }

    public static class Builder
        extends Range.Builder<Builder>
    {
        private DateTime from;

        private DateTime to;

        public Builder from( final DateTime from )
        {
            this.from = from;
            return this;
        }

        public Builder to( final DateTime to )
        {
            this.to = to;
            return this;
        }

        public DateRange build()
        {
            return new DateRange( this );
        }

    }
}
