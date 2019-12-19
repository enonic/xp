package com.enonic.xp.aggregation;

import java.util.Map;

import com.google.common.annotations.Beta;

@Beta
public class CompositeBucket
    extends Bucket
{
    private final Map<String, String> keys;

    private CompositeBucket( final Builder builder )
    {
        super( builder );
        keys = builder.keys;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Map<String, String> getKeys()
    {
        return keys;
    }

    public static final class Builder
        extends Bucket.Builder<Builder>
    {
        private Map<String, String> keys;

        private Builder()
        {
        }

        public Builder keys( final Map<String, String> keys )
        {
            this.keys = keys;
            return this;
        }

        @Override
        public CompositeBucket build()
        {
            return new CompositeBucket( this );
        }
    }
}
