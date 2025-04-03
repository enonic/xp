package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.repository.RepositoryId;

public final class CreateRepositoryIndexParams
{
    private final RepositoryId repositoryId;

    private final RepositorySettings repositorySettings;

    private CreateRepositoryIndexParams( final CreateRepositoryIndexParams.Builder builder )
    {
        repositoryId = builder.repositoryId;
        repositorySettings = builder.repositorySettings == null ? RepositorySettings.create().build() : builder.repositorySettings;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public RepositorySettings getRepositorySettings()
    {
        return repositorySettings;
    }

    public static CreateRepositoryIndexParams.Builder create()
    {
        return new CreateRepositoryIndexParams.Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private RepositorySettings repositorySettings;

        private Builder()
        {
        }

        public CreateRepositoryIndexParams.Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public CreateRepositoryIndexParams.Builder repositorySettings( final RepositorySettings repositorySettings )
        {
            this.repositorySettings = repositorySettings;
            return this;
        }

        public CreateRepositoryIndexParams build()
        {
            return new CreateRepositoryIndexParams( this );
        }
    }
}
