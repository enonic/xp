package com.enonic.xp.repository;

public class IndexConfig
{
    private final IndexResource settings;

    private final IndexResource mapping;

    private IndexConfig( final Builder builder )
    {
        mapping = builder.mapping;
        settings = builder.settings;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IndexResource getSettings()
    {
        return settings;
    }

    public IndexResource getMapping()
    {
        return mapping;
    }

    public static final class Builder
    {
        private IndexResource mapping;

        private IndexResource settings;

        private Builder()
        {
        }

        public Builder indexMapping( final IndexResource val )
        {
            mapping = val;
            return this;
        }

        public Builder indexSettings( final IndexResource val )
        {
            settings = val;
            return this;
        }

        public IndexConfig build()
        {
            return new IndexConfig( this );
        }
    }
}



