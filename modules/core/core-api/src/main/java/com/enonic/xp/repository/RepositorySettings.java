package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

import com.enonic.xp.index.IndexType;

@Beta
public class RepositorySettings
{
    private final ValidationSettings validationSettings;

    private final IndexConfigs indexConfigs;

    private RepositorySettings( final Builder builder )
    {
        validationSettings = builder.validationSettings == null ? createDefaultValidationSettings() : builder.validationSettings;
        indexConfigs = builder.indexConfigs;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ValidationSettings getValidationSettings()
    {
        return validationSettings;
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

    private ValidationSettings createDefaultValidationSettings()
    {
        return ValidationSettings.create().build();
    }

    public static final class Builder
    {
        private ValidationSettings validationSettings;

        private IndexConfigs indexConfigs;

        private Builder()
        {
        }

        public Builder validationSettings( final ValidationSettings val )
        {
            validationSettings = val;
            return this;
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
