package com.enonic.xp.core.query.filter;

public abstract class Filter
{
    private Boolean cache;

    Filter( final Builder builder )
    {
        this.cache = builder.cache;
    }

    public Boolean isCache()
    {
        return cache;
    }

    static class Builder<B extends Builder>
    {
        private Boolean cache;

        @SuppressWarnings("unchecked")
        public B setCache( final boolean cache )
        {
            this.cache = cache;
            return (B) this;
        }


    }

}

