package com.enonic.wem.api.security;

public final class UserStore
{
    private final UserStoreKey key;

    private final String name;

    public UserStore( final Builder builder )
    {
        this.key = builder.key;
        this.name = builder.name;
    }

    public UserStoreKey getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public static Builder newRealm()
    {
        return new Builder();
    }

    public static Builder newRealm( final UserStore userStore )
    {
        return new Builder( userStore );
    }

    public static class Builder
    {
        private UserStoreKey key;

        private String name;

        private Builder()
        {
        }

        private Builder( final UserStore userStore )
        {
            this.key = userStore.key;
            this.name = userStore.name;
        }

        public Builder name( final String value )
        {
            this.name = value;
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
