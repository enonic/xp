package com.enonic.xp.repo.impl;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.context.Context;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class InternalContext
{
    private final RepositoryId repositoryId;

    private final BranchId branchId;

    private final PrincipalKeys principalsKeys;

    private InternalContext( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.branchId = builder.branchId;
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

    public BranchId getBranchId()
    {
        return branchId;
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

    public static Builder create( final InternalContext context )
    {
        return create().
            principalsKeys( context.getPrincipalsKeys() ).
            branch( context.getBranchId() ).
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
        if ( branchId != null ? !branchId.equals( that.branchId ) : that.branchId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = repositoryId != null ? repositoryId.hashCode() : 0;
        result = 31 * result + ( branchId != null ? branchId.hashCode() : 0 );
        return result;
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private BranchId branchId;

        private PrincipalKeys principalsKeys;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder branch( final BranchId branchId )
        {
            this.branchId = branchId;
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
            Preconditions.checkNotNull( branchId, "Branch must be set in internalContext" );
        }

        public InternalContext build()
        {
            this.verify();
            return new InternalContext( this );
        }
    }
}
