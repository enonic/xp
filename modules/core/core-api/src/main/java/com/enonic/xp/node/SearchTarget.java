package com.enonic.xp.node;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class SearchTarget
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final AuthenticationInfo authenticationInfo;

    private SearchTarget( final Builder builder )
    {
        authenticationInfo = builder.authenticationInfo;
        branch = builder.branch;
        repositoryId = builder.repositoryId;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private AuthenticationInfo authenticationInfo;

        private Branch branch;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder authenticationInfo( final AuthenticationInfo val )
        {
            authenticationInfo = val;
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
