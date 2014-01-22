package com.enonic.wem.api.query.aggregation;

public class TermsAggregation
    extends Aggregation
{
    private final Buckets buckets;

    public TermsAggregation( final Builder builder )
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

        public Builder buckets( final Buckets buckets )
        {
            this.buckets = buckets;
            return this;
        }

        public TermsAggregation build()
        {
            return new TermsAggregation( this );
        }
    }

}
