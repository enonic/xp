package com.enonic.wem.api.security;

import com.google.common.base.Preconditions;

public final class User
    extends Principal
{
    private final static User ANONYMOUS = new User();

    private final String email;

    private final String login;

    private final boolean loginDisabled;

    private User( final Builder builder )
    {
        super( builder );
        this.email = builder.email;
        this.login = builder.login;
        this.loginDisabled = builder.loginDisabled;
    }

    private User()
    {
        super( PrincipalKey.ofAnonymous(), "anonymous" );
        this.email = "";
        this.login = "";
        this.loginDisabled = true;
    }

    public String getEmail()
    {
        return email;
    }

    public String getLogin()
    {
        return login;
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

    public static User anonymous()
    {
        return ANONYMOUS;
    }

    public static class Builder
        extends Principal.Builder<Builder>
    {
        private String email;

        private String login;

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
