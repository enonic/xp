package com.enonic.xp.admin.impl.rest.resource.project;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.security.PrincipalKey;

public final class ProjectReadAccess
{
    private ProjectReadAccessType type;

    private List<PrincipalKey> principals;

    private ProjectReadAccess( final Builder builder )
    {
        this.principals = builder.principals.build();
        this.type = builder.type;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ProjectReadAccessType getType()
    {
        return type;
    }

    public List<PrincipalKey> getPrincipals()
    {
        return principals;
    }

    public static final class Builder
    {
        private ImmutableList.Builder<PrincipalKey> principals = ImmutableList.builder();

        private ProjectReadAccessType type;

        public Builder addPrincipals( final Collection<PrincipalKey> principals )
        {
            this.principals.addAll( principals );
            return this;
        }

        public Builder setType( final ProjectReadAccessType type )
        {
            this.type = type;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.type, "readAccessType cannot be null" );
        }

        public ProjectReadAccess build()
        {
            validate();
            return new ProjectReadAccess( this );
        }
    }
}
