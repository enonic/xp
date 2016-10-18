package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

import com.enonic.xp.index.IndexType;

@Beta
public class RepositorySettings
{
    private final IndexConfigs indexConfigs;

    private RepositorySettings( final Builder builder )
    {
        indexConfigs = builder.indexConfigs;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IndexConfigs getIndexConfigs()
    {
        return indexConfigs;
    }

    public IndexSettings getIndexSettings( final IndexType indexType )
    {
        if ( this.indexConfigs == null )
        {
            return null;
        }

        if ( this.indexConfigs.get( indexType ) != null )
        {
            return this.indexConfigs.get( indexType ).getSettings();
        }

        return null;
    }

    public IndexMapping getIndexMappings( final IndexType indexType )
    {
        if ( this.indexConfigs == null )
        {
            return null;
        }

        if ( this.indexConfigs.get( indexType ) != null )
        {
            return this.indexConfigs.get( indexType ).getMapping();
        }

        return null;
    }

    public static final class Builder
    {
        private IndexConfigs indexConfigs;

        private Builder()
        {
        }

        public Builder indexConfigs( final IndexConfigs val )
        {
            indexConfigs = val;
            return this;
        }

        public RepositorySettings build()
        {
            return new RepositorySettings( this );
        }
    }
}
