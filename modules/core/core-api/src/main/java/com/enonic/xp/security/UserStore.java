package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public final class UserStore
{
    private final UserStoreKey key;

    private final String displayName;

    private final String description;

    private final AuthConfig authConfig;

    public UserStore( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.authConfig = builder.authConfig;
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

    public AuthConfig getAuthConfig()
    {
        return authConfig;
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

        private AuthConfig authConfig;

        private Builder()
        {
        }

        private Builder( UserStore userStore )
        {
            key = userStore.key;
            displayName = userStore.displayName;
            description = userStore.description;
            authConfig = userStore.authConfig;
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

        public Builder authConfig( final AuthConfig value )
        {
            this.authConfig = value;
            return this;
        }

        public UserStore build()
        {
            return new UserStore( this );
        }
    }
}
