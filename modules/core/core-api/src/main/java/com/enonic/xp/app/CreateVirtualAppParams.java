package com.enonic.xp.app;

import com.google.common.base.Preconditions;

import com.enonic.xp.repository.RepositoryId;

public class CreateVirtualAppParams
{
    private final RepositoryId repositoryId;

    private CreateVirtualAppParams( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
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

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( repositoryId, "repositoryId must be set" );
        }

        public CreateVirtualAppParams build()
        {
            validate();
            return new CreateVirtualAppParams( this );
        }
    }
}
