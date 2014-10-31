package com.enonic.wem.api.security.auth;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.User;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AuthenticationInfo
{

    private final User user;

    private final PrincipalKeys principals;

    private final boolean authenticated;

    private AuthenticationInfo( final Builder builder )
    {
        this.authenticated = builder.authenticated;
        if ( builder.authenticated )
        {
            this.user = checkNotNull( builder.user, "AuthenticationInfo user cannot be null" );
            builder.principals.add( user.getKey() );
        }
        else
        {
            this.user = null;
        }
        this.principals = PrincipalKeys.from( builder.principals.build() );
    }

    public boolean isAuthenticated()
    {
        return authenticated;
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

    public static Builder create()
    {
        return new Builder( true );
    }

    public static AuthenticationInfo failed()
    {
        return new Builder( false ).build();
    }

    public static class Builder
    {
        private User user;

        private final ImmutableSet.Builder<PrincipalKey> principals;

        private boolean authenticated;

        private Builder( final boolean authenticated )
        {
            this.principals = ImmutableSet.builder();
            this.authenticated = authenticated;
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
