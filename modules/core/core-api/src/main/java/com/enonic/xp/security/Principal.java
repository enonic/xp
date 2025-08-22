package com.enonic.xp.security;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public abstract class Principal
    implements java.security.Principal
{
    private final PrincipalKey key;

    private final String displayName;

    private final Instant modifiedTime;

    private final String description;

    Principal( final Builder<?> builder )
    {
        key = builder.key;
        displayName = builder.displayName;
        modifiedTime = builder.modifiedTime;
        description = builder.description;
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

    public String getDescription()
    {
        return description;
    }

    @Override
    public String getName()
    {
        return key.getId();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Principal ) )
        {
            return false;
        }

        final Principal other = (Principal) o;

        return Objects.equals( key, other.key ) && Objects.equals( displayName, other.displayName ) &&
            Objects.equals( modifiedTime, other.modifiedTime );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, displayName, modifiedTime );
    }

    @Override
    public String toString()
    {
        return key.toString();
    }

    public abstract static class Builder<B>
    {
        PrincipalKey key;

        String displayName;

        Instant modifiedTime;

        String description;

        Builder()
        {
        }

        Builder( final Principal principal )
        {
            this.displayName = principal.displayName;
            this.key = principal.key;
            this.modifiedTime = principal.getModifiedTime();
            this.description = principal.getDescription();
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

        @SuppressWarnings("unchecked")
        public B modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B description( final String description )
        {
            this.description = description;
            return (B) this;
        }

        void validate()
        {
            Objects.requireNonNull( key, "Principal key cannot be null" );
            Objects.requireNonNull( displayName, "Principal display name cannot be null" );
        }
    }
}
