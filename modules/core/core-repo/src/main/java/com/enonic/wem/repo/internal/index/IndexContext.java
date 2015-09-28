package com.enonic.wem.repo.internal.index;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class IndexContext
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final PrincipalKeys principalsKeys;

    private IndexContext( Builder builder )
    {
        repositoryId = builder.repositoryId;
        branch = builder.branch;
        principalsKeys = builder.principalsKeys;
    }

    public static IndexContext from( final Context context )
    {
        return IndexContext.create().
            branch( context.getBranch() ).
            repositoryId( context.getRepositoryId() ).
            principalsKeys( context.getAuthInfo() != null ? context.getAuthInfo().getPrincipals() : PrincipalKeys.empty() ).
            build();
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
        return principalsKeys;
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Branch branch;

        private PrincipalKeys principalsKeys;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder authInfo( final AuthenticationInfo authenticationInfo )
        {
            this.principalsKeys = authenticationInfo != null ? authenticationInfo.getPrincipals() : PrincipalKeys.empty();
            return this;
        }

        public Builder principalsKeys( final PrincipalKeys principalsKeys )
        {
            this.principalsKeys = principalsKeys;
            return this;
        }

        public IndexContext build()
        {
            return new IndexContext( this );
        }
    }
}
