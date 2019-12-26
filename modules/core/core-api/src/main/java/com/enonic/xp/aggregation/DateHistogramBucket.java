package com.enonic.xp.aggregation;

import java.time.Instant;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DateHistogramBucket
    extends Bucket
{
    private final Instant keyAsInstant;

    private DateHistogramBucket( final Builder builder )
    {
        super( builder );
        keyAsInstant = builder.keyAsDate;
    }

    public Instant getKeyAsInstant()
    {
        return keyAsInstant;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends Bucket.Builder<Builder>
    {
        private Instant keyAsDate;

        private Builder()
        {
        }

        public Builder keyAsInstant( final Instant keyAsInstant )
        {
            this.keyAsDate = keyAsInstant;
            return this;
        }

        @Override
        public DateHistogramBucket build()
        {
            return new DateHistogramBucket( this );
        }
    }
}
