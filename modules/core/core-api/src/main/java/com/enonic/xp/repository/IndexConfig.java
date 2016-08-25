package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public class IndexConfig
{
    private final IndexSettings settings;

    private final IndexMapping mapping;

    private IndexConfig( final Builder builder )
    {
        settings = builder.settings;
        mapping = builder.mapping;
    }

    public IndexSettings getSettings()
    {
        return settings;
    }

    public IndexMapping getMapping()
    {
        return mapping;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private IndexSettings settings;

        private IndexMapping mapping;

        private Builder()
        {
        }

        public Builder settings( final IndexSettings val )
        {
            settings = val;
            return this;
        }

        public Builder mapping( final IndexMapping val )
        {
            mapping = val;
            return this;
        }

        public IndexConfig build()
        {
            return new IndexConfig( this );
        }
    }
}



