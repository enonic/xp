package com.enonic.wem.api.security;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Principal
{
    private final PrincipalKey key;

    private final String displayName;

    Principal( final PrincipalKey principalKey, final String displayName )
    {
        this.key = checkNotNull( principalKey, "Principal key cannot be null" );
        this.displayName = checkNotNull( displayName, "Principal display name cannot be null" );
    }

    Principal( final Builder builder )
    {
        key = builder.key;
        displayName = builder.displayName;
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public static abstract class Builder<B>
    {
        PrincipalKey key;

        String displayName;

        Builder()
        {
        }

        Builder( final Principal principal )
        {
            this.displayName = principal.displayName;
            this.key = principal.key;
        }

        @SuppressWarnings("unchecked")
        public B key( final PrincipalKey key )
        {
            this.key = key;
            return (B) this;
        }


        @SuppressWarnings("unchecked")
        public B displayName( final String displayName )
        {
            this.displayName = displayName;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( displayName );
        }
    }
}
