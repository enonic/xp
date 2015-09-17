package com.enonic.wem.repo.internal;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class InternalContext
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final PrincipalKeys principalsKeys;

    private InternalContext( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.branch = builder.branch;
        this.principalsKeys = builder.principalsKeys;
    }

    public static InternalContext from( final Context context )
    {
        return InternalContext.create().
            branch( context.getBranch() ).
            repositoryId( context.getRepositoryId() ).
            principalsKeys( context.getAuthInfo() != null ? context.getAuthInfo().getPrincipals() : PrincipalKeys.empty() ).
            build();
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public PrincipalKeys getPrincipalsKeys()
    {
        return principalsKeys;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Context context )
    {
        return create().
            authInfo( context.getAuthInfo() ).
            branch( context.getBranch() ).
            repositoryId( context.getRepositoryId() );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof InternalContext ) )
        {
            return false;
        }

        final InternalContext that = (InternalContext) o;

        if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
        {
            return false;
        }
        if ( branch != null ? !branch.equals( that.branch ) : that.branch != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = repositoryId != null ? repositoryId.hashCode() : 0;
        result = 31 * result + ( branch != null ? branch.hashCode() : 0 );
        return result;
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

        private void verify()
        {
            Preconditions.checkNotNull( repositoryId, "Repository must be set in internalContext" );
            Preconditions.checkNotNull( branch, "Branch must be set in internalContext" );
        }

        public InternalContext build()
        {
            this.verify();
            return new InternalContext( this );
        }
    }
}
