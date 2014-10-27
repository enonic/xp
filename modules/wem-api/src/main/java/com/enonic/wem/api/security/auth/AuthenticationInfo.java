package com.enonic.wem.api.security.auth;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.User;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AuthenticationInfo
{
    private User user;

    private PrincipalKeys principals;

    private AuthenticationInfo( final Builder builder )
    {
        this.user = checkNotNull( builder.user, "AuthenticationInfo user cannot be null" );
        builder.principals.add( user.getKey() );
        this.principals = PrincipalKeys.from( builder.principals.build() );
    }

    public User getUser()
    {
        return user;
    }

    public PrincipalKeys getPrincipals()
    {
        return principals;
    }

    public boolean hasRole( final String role )
    {
        return principals.stream().anyMatch( principal -> principal.isRole() && principal.getId().equals( role ) );
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static Builder newBuilder( final AuthenticationInfo info )
    {
        return new Builder( info );
    }

    public static class Builder
    {
        private User user;

        private final ImmutableSet.Builder<PrincipalKey> principals;

        private Builder()
        {
            principals = ImmutableSet.builder();
        }

        private Builder( final AuthenticationInfo info )
        {
            this.user = info.user;
            this.principals = ImmutableSet.builder();
            this.principals.addAll( info.principals.getSet() );
        }

        public Builder user( final User user )
        {
            this.user = user;
            return this;
        }

        public Builder principal( final PrincipalKey principal )
        {
            this.principals.add( principal );
            return this;
        }

        public Builder principals( final Iterable<PrincipalKey> principals )
        {
            this.principals.addAll( principals );
            return this;
        }

        public AuthenticationInfo build()
        {
            return new AuthenticationInfo( this );
        }
    }
}
