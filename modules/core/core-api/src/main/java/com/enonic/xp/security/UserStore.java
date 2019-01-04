package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public final class UserStore
{
    private final UserStoreKey key;

    private final String displayName;

    private final String description;

    private final IdProviderConfig idProviderConfig;

    public UserStore( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.idProviderConfig = builder.idProviderConfig;
    }

    public UserStoreKey getKey()
    {
        return key;
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

    public static Builder create( UserStore userStore )
    {
        return new Builder( userStore );
    }

    public static class Builder
    {
        private UserStoreKey key;

        private String displayName;

        private String description;

        private IdProviderConfig idProviderConfig;

        private Builder()
        {
        }

        private Builder( UserStore userStore )
        {
            key = userStore.key;
            displayName = userStore.displayName;
            description = userStore.description;
            idProviderConfig = userStore.idProviderConfig;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder key( final UserStoreKey value )
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

        public UserStore build()
        {
            return new UserStore( this );
        }
    }
}
