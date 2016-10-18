package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

import com.enonic.xp.index.IndexType;

@Beta
public class RepositorySettings
{
    private final ValidationSettings validationSettings;

    private final IndexDefinitions indexDefinitions;

    private RepositorySettings( final Builder builder )
    {
        validationSettings = builder.validationSettings == null ? createDefaultValidationSettings() : builder.validationSettings;
        indexDefinitions = builder.indexDefinitions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ValidationSettings getValidationSettings()
    {
        return validationSettings;
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

    private ValidationSettings createDefaultValidationSettings()
    {
        return ValidationSettings.create().build();
    }

    public static final class Builder
    {
        private ValidationSettings validationSettings;

        private IndexDefinitions indexDefinitions;

        private Builder()
        {
        }

        public Builder validationSettings( final ValidationSettings val )
        {
            validationSettings = val;
            return this;
        }

        public Builder indexConfigs( final IndexDefinitions val )
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
