package com.enonic.xp.aggregation;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class GeoDistanceRangeBucket
    extends Bucket
{
    private final Number from;

    private final Number to;

    private GeoDistanceRangeBucket( final Builder builder )
    {
        super( builder );
        from = builder.from;
        to = builder.to;
    }

    public Number getFrom()
    {
        return from;
    }

    public Number getTo()
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
        private Number from;

        private Number to;

        private Builder()
        {
        }

        public Builder from( Number from )
        {
            this.from = from;
            return this;
        }

        public Builder to( Number to )
        {
            this.to = to;
            return this;
        }

        @Override
        public GeoDistanceRangeBucket build()
        {
            return new GeoDistanceRangeBucket( this );
        }
    }
}
