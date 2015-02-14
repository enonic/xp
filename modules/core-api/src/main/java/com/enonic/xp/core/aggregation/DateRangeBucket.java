package com.enonic.xp.core.aggregation;

import java.time.Instant;

public class DateRangeBucket
    extends Bucket
{
    private final Instant from;

    private final Instant to;

    private DateRangeBucket( final Builder builder )
    {
        super( builder );
        from = builder.from;
        to = builder.to;
    }

    public Instant getFrom()
    {
        return from;
    }

    public Instant getTo()
    {
        return to;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends Bucket.Builder<Builder>
    {
        private Instant from;

        private Instant to;

        private Builder()
        {
        }

        public Builder from( final Instant from )
        {
            this.from = from;
            return this;
        }

        public Builder to( final Instant to )
        {
            this.to = to;
            return this;
        }

        public DateRangeBucket build()
        {
            return new DateRangeBucket( this );
        }
    }
}
