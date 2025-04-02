package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.repo.impl.index.IndexMapping;
import com.enonic.xp.repo.impl.index.IndexSettings;

public final class IndexDefinition
{
    private final IndexSettings settings;

    private final IndexMapping mapping;

    private IndexDefinition( final Builder builder )
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

        public IndexDefinition build()
        {
            return new IndexDefinition( this );
        }
    }
}



