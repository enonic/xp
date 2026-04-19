package com.enonic.xp.repo.impl;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.Objects.requireNonNull;

@NullMarked
public class SingleRepoSearchSource
    implements SearchSource
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final PrincipalKeys acl;

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public PrincipalKeys getAcl()
    {
        return acl;
    }

    private SingleRepoSearchSource( final Builder builder )
    {
        repositoryId = requireNonNull( builder.repositoryId, "repositoryId is required in search-source" );
        branch = requireNonNull( builder.branch, "branch is required in search-source" );
        acl = requireNonNull( builder.acl, "acl is required in search-source" );
    }

    public static SingleRepoSearchSource from( final InternalContext context )
    {
        return create().repositoryId( context.getRepositoryId() ).branch( context.getBranch() ).acl( context.getPrincipalKeys() ).build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private @Nullable RepositoryId repositoryId;

        private @Nullable Branch branch;

        private @Nullable PrincipalKeys acl;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder branch( final Branch val )
        {
            branch = val;
            return this;
        }

        public Builder acl( final PrincipalKeys val )
        {
            acl = val;
            return this;
        }

        public SingleRepoSearchSource build()
        {
            return new SingleRepoSearchSource( this );
        }
    }
}
