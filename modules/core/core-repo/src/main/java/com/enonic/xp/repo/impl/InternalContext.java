package com.enonic.xp.repo.impl;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

public class InternalContext
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final PrincipalKeys principalsKeys;

    private final boolean skipConstraints;

    private final SearchPreference searchPreference;

    private InternalContext( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.branch = builder.branch;
        this.principalsKeys = Objects.requireNonNullElse( builder.principalsKeys, PrincipalKeys.empty() );
        this.skipConstraints = builder.skipConstraints;
        this.searchPreference = builder.searchPreference;
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

    public boolean isSkipConstraints()
    {
        return skipConstraints;
    }

    public static Builder create( final InternalContext context )
    {
        return create().principalsKeys( context.getPrincipalsKeys() )
            .branch( context.getBranch() )
            .repositoryId( context.getRepositoryId() )
            .skipConstraints( context.skipConstraints )
            .searchPreference( context.searchPreference );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Context context )
    {
        return create()
            .principalsKeys( context.getAuthInfo() != null ? context.getAuthInfo().getPrincipals() : PrincipalKeys.empty() )
            .branch( context.getBranch() )
            .repositoryId( context.getRepositoryId() );
    }

    public SearchPreference getSearchPreference()
    {
        return searchPreference;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final InternalContext that = (InternalContext) o;
        return skipConstraints == that.skipConstraints && Objects.equals( repositoryId, that.repositoryId ) &&
            Objects.equals( branch, that.branch ) && Objects.equals( principalsKeys, that.principalsKeys ) &&
            Objects.equals( searchPreference, that.searchPreference );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( repositoryId, branch, principalsKeys, skipConstraints, searchPreference );
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private Branch branch;

        private PrincipalKeys principalsKeys;

        private boolean skipConstraints;

        private SearchPreference searchPreference;

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

        public Builder principalsKeys( final PrincipalKeys principalsKeys )
        {
            this.principalsKeys = principalsKeys;
            return this;
        }

        public Builder skipConstraints( final boolean skip )
        {
            this.skipConstraints = skip;
            return this;
        }

        public Builder searchPreference( final SearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return this;
        }

        private void verify()
        {
            Objects.requireNonNull( repositoryId, "repositoryId is required" );
            Objects.requireNonNull( branch, "branch is required" );
        }

        public InternalContext build()
        {
            this.verify();
            return new InternalContext( this );
        }
    }
}
