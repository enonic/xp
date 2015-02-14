package com.enonic.xp.core.aggregation;

public class BucketAggregation
    extends Aggregation
{
    private final Buckets buckets;

    private BucketAggregation( final Builder builder )
    {
        super( builder );
        this.buckets = builder.buckets;
    }

    public Buckets getBuckets()
    {
        return buckets;
    }

    public static class Builder
        extends Aggregation.Builder<Builder>
    {
        private Buckets buckets;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder buckets( final Buckets buckets )
        {
            this.buckets = buckets;
            return this;
        }

        public BucketAggregation build()
        {
            return new BucketAggregation( this );
        }

    }


}
