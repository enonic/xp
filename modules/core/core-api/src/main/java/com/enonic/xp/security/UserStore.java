package com.enonic.xp.security;

import com.google.common.annotations.Beta;

@Beta
public final class UserStore
{
    private final UserStoreKey key;

    private final String displayName;

    private final String authServiceKey;

    public UserStore( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.authServiceKey = builder.authServiceKey;
    }

    public UserStoreKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getAuthServiceKey()
    {
        return authServiceKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final UserStore userStore )
    {
        return new Builder( userStore );
    }

    public static class Builder
    {
        private UserStoreKey key;

        private String displayName;

        private String authServiceKey;

        private Builder()
        {
        }

        private Builder( final UserStore userStore )
        {
            this.key = userStore.key;
            this.displayName = userStore.displayName;
            this.authServiceKey = userStore.authServiceKey;
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

        public Builder authServiceKey( final String value )
        {
            this.authServiceKey = value;
            return this;
        }

        public UserStore build()
        {
            return new UserStore( this );
        }
    }

}
