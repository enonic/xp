package com.enonic.wem.api.query.aggregation;

import org.joda.time.DateTime;

public class DateRange
    extends Range
{
    private final Object from;

    private final Object to;

    public DateRange( final Builder builder )
    {
        super( builder );
        this.from = builder.from;
        this.to = builder.to;
    }

    public Object getFrom()
    {
        return from;
    }

    public Object getTo()
    {
        return to;
    }

    public static class Builder
        extends Range.Builder<Builder>
    {
        private Object from;

        private Object to;

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

        public Builder from( final String from )
        {
            this.from = from;
            return this;
        }

        public Builder to( final String to )
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
