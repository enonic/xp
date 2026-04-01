package com.enonic.xp.node;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

@NullMarked
public final class SearchTarget
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final PrincipalKeys principalKeys;

    private SearchTarget( final Builder builder )
    {
        this.principalKeys = Objects.requireNonNullElse( builder.principalKeys, PrincipalKeys.empty() );
        this.branch = Objects.requireNonNull( builder.branch, "branch is required" );
        this.repositoryId = Objects.requireNonNull( builder.repositoryId, "repositoryId is required" );
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
        private @Nullable PrincipalKeys principalKeys;

        private @Nullable Branch branch;

        private @Nullable RepositoryId repositoryId;

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

        public SearchTarget build()
        {
            return new SearchTarget( this );
        }
    }
}
