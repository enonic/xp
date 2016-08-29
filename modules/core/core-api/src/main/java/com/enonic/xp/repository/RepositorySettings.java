package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public class RepositorySettings
{
    private final RepositoryId repositoryId;

    private RepositorySettings( final Builder builder )
    {
        repositoryId = builder.repositoryId;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public RepositorySettings build()
        {
            return new RepositorySettings( this );
        }
    }
}
