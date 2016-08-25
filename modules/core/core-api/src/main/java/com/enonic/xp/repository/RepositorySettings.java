package com.enonic.xp.repository;

public class RepositorySettings
{
    private final RepositoryId repositoryId;

    private final IndexConfigs indexConfigs;

    private RepositorySettings( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        indexConfigs = builder.indexConfigs;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public IndexConfigs getIndexConfigs()
    {
        return indexConfigs;
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private IndexConfigs indexConfigs;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
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
