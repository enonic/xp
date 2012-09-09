package com.enonic.wem.api.userstore;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

public final class UserStores
    implements Iterable<UserStore>
{
    private final ImmutableList<UserStore> list;

    private UserStores( final ImmutableList<UserStore> list )
    {
        this.list = list;
    }

    public int getSize()
    {
        return this.list.size();
    }

    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    public UserStore getFirst()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public List<UserStore> getList()
    {
        return this.list;
    }

    public UserStoreNames getNames()
    {
        final Collection<UserStoreName> names = Collections2.transform( this.list, new ToNameFunction() );
        return UserStoreNames.from( names );
    }

    @Override
    public Iterator<UserStore> iterator()
    {
        return this.list.iterator();
    }

    public int hashCode()
    {
        return this.list.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof UserStores ) && this.list.equals( ( (UserStores) o ).list );
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
