package com.enonic.wem.repo.internal.index;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.workspace.Workspace;

public class IndexContext
{
    private final RepositoryId repositoryId;

    private final Workspace workspace;

    private final PrincipalKeys principalsKeys;

    private IndexContext( Builder builder )
    {
        repositoryId = builder.repositoryId;
        workspace = builder.workspace;
        principalsKeys = builder.principalsKeys;
    }

    public static IndexContext from( final Context context )
    {
        return IndexContext.create().
            workspace( context.getWorkspace() ).
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

    public Workspace getWorkspace()
    {
        return workspace;
    }

    public PrincipalKeys getPrincipalKeys()
    {
        return principalsKeys;
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Workspace workspace;

        private PrincipalKeys principalsKeys;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder workspace( final Workspace workspace )
        {
            this.workspace = workspace;
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
