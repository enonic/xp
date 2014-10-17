package com.enonic.wem.api.identity;

import com.google.common.base.Preconditions;

public abstract class Identity
{
    private final IdentityKey key;

    private final String displayName;

    protected Identity( final Builder builder )
    {
        Preconditions.checkNotNull( builder.identityKey, "Identity key cannot be null" );
        Preconditions.checkNotNull( builder.displayName, "Identity display name cannot be null" );

        this.key = builder.identityKey;
        this.displayName = builder.displayName;
    }

    public IdentityKey getIdentityKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public static class Builder
    {
        private IdentityKey identityKey;

        private String displayName;

        protected Builder()
        {
        }

        protected Builder( final Identity identity )
        {
            this.displayName = identity.getDisplayName();
            this.identityKey = identity.getIdentityKey();
        }

        public Builder identityKey( final IdentityKey value )
        {
            this.identityKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

    }
}
