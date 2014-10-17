package com.enonic.wem.api.identity;

import com.google.common.base.Preconditions;

public final class User
    extends Account
{
    private final String email;

    private User( final Builder builder )
    {
        super( builder.account );
        Preconditions.checkArgument( getIdentityKey().isUser(), "Invalid Identity Type for User: " + getIdentityKey().getType() );
        this.email = builder.email;
    }

    public String getEmail()
    {
        return email;
    }

    public static Builder newUser()
    {
        return new Builder();
    }

    public static Builder newUser( final User user )
    {
        return new Builder( user );
    }

    public static class Builder
    {
        private Account.Builder account;

        private String email;

        private Builder()
        {
            account = new Account.Builder();
        }

        private Builder( final User user )
        {
            account = new Account.Builder( user );
            this.email = user.email;
        }

        public Builder identityKey( final IdentityKey value )
        {
            this.account.identity.identityKey( value );
            return this;
        }

        public Builder displayName( final String value )
        {
            this.account.identity.displayName( value );
            return this;
        }

        public Builder login( final String value )
        {
            this.account.login( value );
            return this;
        }

        public Builder email( final String value )
        {
            this.email = value;
            return this;
        }

        public User build()
        {
            return new User( this );
        }
    }

}
