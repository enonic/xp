package com.enonic.xp.query.aggregation;

import com.google.common.annotations.Beta;

@Beta
public abstract class Range
{
    private final String key;

    protected Range( final Builder builder )
    {
        this.key = builder.key;
    }

    public String getKey()
    {
        return key;
    }

    public static class Builder<T extends Builder>
    {
        private String key;

        public T key( final String key )
        {
            this.key = key;
            return (T) this;
        }
    }

}
