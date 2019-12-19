package com.enonic.xp.aggregation;

import java.util.Map;

import com.google.common.annotations.Beta;

@Beta
public class CompositeBucketAggregation
    extends BucketAggregation
{
    private final Map<String, Object> after;

    protected CompositeBucketAggregation( final Builder builder )
    {
        super( builder );
        this.after = builder.after;
    }

    public Map<String, Object> getAfter()
    {
        return after;
    }

    public static class Builder
        extends BucketAggregation.Builder
    {
        private Map<String, Object> after;

        public Builder( final String name )
        {
            super( name );
        }

        public Builder after( final Map<String, Object> after )
        {
            this.after = after;
            return this;
        }

        public Builder buckets( final Buckets buckets )
        {
            super.buckets( buckets );
            return this;
        }

        public CompositeBucketAggregation build()
        {
            return new CompositeBucketAggregation( this );
        }

    }


}
