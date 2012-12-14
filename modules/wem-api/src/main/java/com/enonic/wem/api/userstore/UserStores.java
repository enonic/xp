package com.enonic.wem.api.userstore;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.AbstractImmutableEntityList;

public final class UserStores
    extends AbstractImmutableEntityList<UserStore>
{

    private UserStores( final ImmutableList<UserStore> list )
    {
        super( list );
    }

    public UserStoreNames getNames()
    {
        final Collection<UserStoreName> names = Collections2.transform( this.list, new ToNameFunction() );
        return UserStoreNames.from( names );
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

    public static UserStores from( final Iterable<UserStore> userStores )
    {
        return new UserStores( ImmutableList.copyOf( userStores ) );
    }

    public static UserStores from( final Collection<UserStore> userStores )
    {
        return new UserStores( ImmutableList.copyOf( userStores ) );
    }

    private final static class ToNameFunction
        implements Function<UserStore, UserStoreName>
    {
        @Override
        public UserStoreName apply( final UserStore value )
        {
            return value.getName();
        }
    }
}
