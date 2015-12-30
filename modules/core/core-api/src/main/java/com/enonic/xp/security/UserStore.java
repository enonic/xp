package com.enonic.xp.security;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;

@Beta
public final class UserStore
{
    private final UserStoreKey key;

    private final String displayName;

    private final ApplicationKey authApplication;

    public UserStore( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
        this.authApplication = builder.authApplication;
    }

    public UserStoreKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public ApplicationKey getAuthApplication()
    {
        return authApplication;
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

        private ApplicationKey authApplication;

        private Builder()
        {
        }

        private Builder( final UserStore userStore )
        {
            this.key = userStore.key;
            this.displayName = userStore.displayName;
            this.authApplication = userStore.authApplication;
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

        public Builder authApplication( final ApplicationKey value )
        {
            this.authApplication = value;
            return this;
        }

        public UserStore build()
        {
            return new UserStore( this );
        }
    }

}
