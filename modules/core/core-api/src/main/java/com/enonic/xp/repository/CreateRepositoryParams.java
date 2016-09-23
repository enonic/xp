package com.enonic.xp.repository;

import com.google.common.base.Preconditions;

public class CreateRepositoryParams
{
    private final RepositoryId repositoryId;

    private final RepositorySettings repositorySettings;

    private CreateRepositoryParams( final Builder builder )
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

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private RepositoryId repositoryId;

        private RepositorySettings repositorySettings;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder repositorySettings( final RepositorySettings repositorySettings )
        {
            this.repositorySettings = repositorySettings;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( repositoryId, "repositoryId cannot be null" );
        }

        public CreateRepositoryParams build()
        {
            validate();
            return new CreateRepositoryParams( this );
        }
    }
}
