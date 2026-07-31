package com.enonic.xp.app;

import java.time.Instant;

import static java.util.Objects.requireNonNull;


public final class Namespace
{
    private final ApplicationKey key;

    private final String description;

    private final Instant modifiedTime;

    private Namespace( final Builder builder )
    {
        this.key = builder.key;
        this.description = builder.description;
        this.modifiedTime = builder.modifiedTime;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplicationKey getKey()
    {
        return key;
    }

    public String getDescription()
    {
        return description;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public static final class Builder
    {
        private ApplicationKey key;

        private String description;

        private Instant modifiedTime;

        private Builder()
        {
        }

        public Builder key( final ApplicationKey key )
        {
            this.key = key;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
        }

        public Namespace build()
        {
            validate();
            return new Namespace( this );
        }
    }
}
