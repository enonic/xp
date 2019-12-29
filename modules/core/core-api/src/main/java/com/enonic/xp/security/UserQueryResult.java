package com.enonic.xp.security;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class UserQueryResult
{
    private final int totalSize;

    private final Principals users;

    private UserQueryResult( final Builder builder )
    {
        this.totalSize = builder.totalSize;
        this.users = Principals.from( builder.users.build() );
    }

    public Principals getUsers()
    {
        return users;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public boolean isEmpty()
    {
        return users.isEmpty();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int totalSize;

        private final ImmutableList.Builder<Principal> users;

        private Builder()
        {
            this.users = ImmutableList.builder();
        }

        public Builder addUser( final Principal user )
        {
            this.users.add( user );
            return this;
        }

        public Builder addUsers( final Iterable<Principal> users )
        {
            this.users.addAll( users );
            return this;
        }

        public Builder totalSize( final int totalSize )
        {
            this.totalSize = totalSize;
            return this;
        }

        public UserQueryResult build()
        {
            return new UserQueryResult( this );
        }
    }

}
