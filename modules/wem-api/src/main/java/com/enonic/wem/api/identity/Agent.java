package com.enonic.wem.api.identity;

import com.google.common.base.Preconditions;

public final class Agent
    extends Account
{
    private Agent( final Builder builder )
    {
        super( builder.account );
        Preconditions.checkArgument( getIdentityKey().isAgent(), "Invalid Identity Type for Agent: " + getIdentityKey().getType() );
    }

    public static Builder newAgent()
    {
        return new Builder();
    }

    public static Builder newAgent( final Agent agent )
    {
        return new Builder( agent );
    }

    public static class Builder
    {
        private Account.Builder account;

        private Builder()
        {
            account = new Account.Builder();
        }

        private Builder( final Agent agent )
        {
            account = new Account.Builder( agent );
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

        public Agent build()
        {
            return new Agent( this );
        }
    }
}
