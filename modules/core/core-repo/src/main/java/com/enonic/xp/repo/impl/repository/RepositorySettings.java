package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexMapping;
import com.enonic.xp.repo.impl.index.IndexSettings;

public final class RepositorySettings
{
    private final IndexDefinitions indexDefinitions;

    private RepositorySettings( final Builder builder )
    {
        indexDefinitions = builder.indexDefinitions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IndexDefinitions getIndexDefinitions()
    {
        return indexDefinitions;
    }

    public IndexSettings getIndexSettings( final IndexType indexType )
    {
        if ( this.indexDefinitions == null )
        {
            return null;
        }

        if ( this.indexDefinitions.get( indexType ) != null )
        {
            return this.indexDefinitions.get( indexType ).getSettings();
        }

        return null;
    }

    public IndexMapping getIndexMappings( final IndexType indexType )
    {
        if ( this.indexDefinitions == null )
        {
            return null;
        }

        if ( this.indexDefinitions.get( indexType ) != null )
        {
            return this.indexDefinitions.get( indexType ).getMapping();
        }

        return null;
    }

    public static final class Builder
    {
        private IndexDefinitions indexDefinitions;

        private Builder()
        {
        }

        public Builder indexDefinitions( final IndexDefinitions val )
        {
            indexDefinitions = val;
            return this;
        }

        public RepositorySettings build()
        {
            return new RepositorySettings( this );
        }
    }
}
