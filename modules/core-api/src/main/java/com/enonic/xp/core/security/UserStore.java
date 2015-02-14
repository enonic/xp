package com.enonic.xp.core.security;

public final class UserStore
{
    private final UserStoreKey key;

    private final String displayName;

    public UserStore( final Builder builder )
    {
        this.key = builder.key;
        this.displayName = builder.displayName;
    }

    public UserStoreKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public static Builder newUserStore()
    {
        return new Builder();
    }

    public static Builder newUserStore( final UserStore userStore )
    {
        return new Builder( userStore );
    }

    public static class Builder
    {
        private UserStoreKey key;

        private String displayName;

        private Builder()
        {
        }

        private Builder( final UserStore userStore )
        {
            this.key = userStore.key;
            this.displayName = userStore.displayName;
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

        public UserStore build()
        {
            return new UserStore( this );
        }
    }

}
