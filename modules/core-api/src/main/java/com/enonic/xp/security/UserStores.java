package com.enonic.xp.security;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;

public final class UserStores
    extends AbstractImmutableEntityList<UserStore>
{
    private final ImmutableMap<UserStoreKey, UserStore> map;

    private UserStores( final ImmutableList<UserStore> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, UserStore::getKey );
    }

    public UserStoreKeys getKeys()
    {
        return UserStoreKeys.from( map.keySet() );
    }

    public UserStore getUserStore( final UserStoreKey UserStoreKey )
    {
        return map.get( UserStoreKey );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static UserStores empty()
    {
        final ImmutableList<UserStore> list = ImmutableList.of();
        return new UserStores( list );
    }

    public static UserStores from( final UserStore... userStores )
    {
        return new UserStores( ImmutableList.copyOf( userStores ) );
    }

    public static UserStores from( final Iterable<? extends UserStore> userStores )
    {
        return new UserStores( ImmutableList.copyOf( userStores ) );
    }

}
