package com.enonic.xp.security;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class IdProvider
{
    private final IdProviderKey key;

    private final String displayName;

    private final String description;

    private final IdProviderConfig idProviderConfig;

    public IdProvider( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.idProviderConfig = builder.idProviderConfig;
    }

    public static Builder create( IdProvider idProvider )
    {
        return new Builder( idProvider );
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public IdProviderConfig getIdProviderConfig()
    {
        return idProviderConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public IdProviderKey getKey()
    {
        return key;
    }

    public static class Builder
    {
        private IdProviderKey key;

        private String displayName;

        private String description;

        private IdProviderConfig idProviderConfig;

        private Builder()
        {
        }

        private Builder( IdProvider idProvider )
        {
            key = idProvider.key;
            displayName = idProvider.displayName;
            description = idProvider.description;
            idProviderConfig = idProvider.idProviderConfig;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder key( final IdProviderKey value )
        {
            this.key = value;
            return this;
        }

        public Builder description( final String value )

        {
            this.description = value;
            return this;
        }

        public Builder idProviderConfig( final IdProviderConfig value )
        {
            this.idProviderConfig = value;
            return this;
        }

        public IdProvider build()
        {
            return new IdProvider( this );
        }
    }
}
