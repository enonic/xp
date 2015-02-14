package com.enonic.xp.core.security;

import com.google.common.base.Preconditions;

import static java.util.Objects.requireNonNull;

public final class User
    extends Principal
{

    public final static User ANONYMOUS = User.create().
        key( PrincipalKey.ofAnonymous() ).
        displayName( "Anonymous User" ).
        login( "anonymous" ).
        build();

    private final String email;

    private final String login;

    private final String authenticationHash;

    private final boolean loginDisabled;

    private User( final Builder builder )
    {
        super( builder );
        Preconditions.checkNotNull( builder.login, "login is required for a User" );
        this.email = builder.email;
        this.login = requireNonNull( builder.login );
        this.loginDisabled = builder.loginDisabled;
        this.authenticationHash = builder.authenticationHash;
    }

    public String getEmail()
    {
        return email;
    }

    public String getLogin()
    {
        return login;
    }

    public String getAuthenticationHash()
    {
        return authenticationHash;
    }

    public boolean isDisabled()
    {
        return loginDisabled;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final User user )
    {
        return new Builder( user );
    }

    public static class Builder
        extends Principal.Builder<Builder>
    {
        private String email;

        private String login;

        private String authenticationHash;

        private boolean loginDisabled;

        private Builder()
        {
            super();
        }

        private Builder( final User user )
        {
            super( user );
            this.email = user.getEmail();
            this.login = user.getLogin();
            this.authenticationHash = user.getAuthenticationHash();
            this.loginDisabled = user.isDisabled();
        }

        public Builder login( final String value )
        {
            this.login = value;
            return this;
        }

        public Builder email( final String value )
        {
            this.email = value;
            return this;
        }

        public Builder authenticationHash( final String value )
        {
            this.authenticationHash = value;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Preconditions.checkArgument( this.key.isUser(), "Invalid Principal Type for User: " + this.key.getType() );
        }

        public User build()
        {
            return new User( this );
        }
    }

}
