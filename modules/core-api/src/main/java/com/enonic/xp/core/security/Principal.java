package com.enonic.xp.core.security;

import java.time.Instant;

import com.google.common.base.Preconditions;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Principal
{
    private final PrincipalKey key;

    private final String displayName;

    private final Instant modifiedTime;

    Principal( final PrincipalKey principalKey, final String displayName, final Instant modifiedTime )
    {
        this.key = checkNotNull( principalKey, "Principal key cannot be null" );
        this.displayName = checkNotNull( displayName, "Principal display name cannot be null" );
        this.modifiedTime = modifiedTime;
    }

    Principal( final Builder builder )
    {
        key = builder.key;
        displayName = builder.displayName;
        modifiedTime = builder.modifiedTime;
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public static abstract class Builder<B>
    {
        PrincipalKey key;

        String displayName;

        Instant modifiedTime;

        Builder()
        {
        }

        Builder( final Principal principal )
        {
            this.displayName = principal.displayName;
            this.key = principal.key;
            this.modifiedTime = principal.getModifiedTime();
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

        public B modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return (B) this;
        }

        void validate()
        {
            Preconditions.checkNotNull( key );
            Preconditions.checkNotNull( displayName );
        }
    }
}
