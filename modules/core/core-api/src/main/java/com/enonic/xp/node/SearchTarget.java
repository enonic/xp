package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

public final class SearchTarget
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final PrincipalKeys principalKeys;

    private SearchTarget( final Builder builder )
    {
        principalKeys = builder.principalKeys;
        branch = builder.branch;
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

    public Branch getBranch()
    {
        return branch;
    }

    public PrincipalKeys getPrincipalKeys()
    {
        return principalKeys;
    }

    public static final class Builder
    {
        private PrincipalKeys principalKeys;

        private Branch branch;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder principalKeys( final PrincipalKeys val )
        {
            principalKeys = val;
            return this;
        }

        public Builder branch( final Branch val )
        {
            branch = val;
            return this;
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.principalKeys, "Auth info must be set" );
            Preconditions.checkNotNull( this.branch, "Branch must be set" );
            Preconditions.checkNotNull( this.repositoryId, "RepositoryId must be set" );
        }

        public SearchTarget build()
        {
            validate();
            return new SearchTarget( this );
        }
    }
}
