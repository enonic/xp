package com.enonic.wem.api.userstore.connector;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public final class UserStoreConnectors
    implements Iterable<UserStoreConnector>
{
    private final ImmutableList<UserStoreConnector> list;

    private UserStoreConnectors( final ImmutableList<UserStoreConnector> list )
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

    public UserStoreConnector getFirst()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public List<UserStoreConnector> getList()
    {
        return this.list;
    }

    public Set<String> getNames()
    {
        final Collection<String> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    @Override
    public Iterator<UserStoreConnector> iterator()
    {
        return this.list.iterator();
    }

    @Override
    public int hashCode()
    {
        return this.list.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof UserStoreConnectors ) && this.list.equals( ( (UserStoreConnectors) o ).list );
    }

    public static UserStoreConnectors empty()
    {
        final ImmutableList<UserStoreConnector> list = ImmutableList.of();
        return new UserStoreConnectors( list );
    }

    public static UserStoreConnectors from( final UserStoreConnector... connectors )
    {
        return new UserStoreConnectors( ImmutableList.copyOf( connectors ) );
    }

    public static UserStoreConnectors from( final Iterable<UserStoreConnector> connectors )
    {
        return new UserStoreConnectors( ImmutableList.copyOf( connectors ) );
    }

    public static UserStoreConnectors from( final Collection<UserStoreConnector> connectors )
    {
        return new UserStoreConnectors( ImmutableList.copyOf( connectors ) );
    }

    private final static class ToNameFunction
        implements Function<UserStoreConnector, String>
    {
        @Override
        public String apply( final UserStoreConnector value )
        {
            return value.getName();
        }
    }
}