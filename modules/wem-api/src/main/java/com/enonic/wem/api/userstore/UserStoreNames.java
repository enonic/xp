package com.enonic.wem.api.userstore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class UserStoreNames
    implements Iterable<UserStoreName>
{
    private final ImmutableSet<UserStoreName> set;

    private UserStoreNames( final ImmutableSet<UserStoreName> set )
    {
        this.set = set;
    }

    public int getSize()
    {
        return this.set.size();
    }

    public boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    public UserStoreName getFirst()
    {
        return this.set.isEmpty() ? null : this.set.iterator().next();
    }

    public boolean contains( final UserStoreName name )
    {
        return this.set.contains( name );
    }

    public Set<UserStoreName> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<UserStoreName> iterator()
    {
        return this.set.iterator();
    }

    public UserStoreNames add( final String... names )
    {
        return add( parseNames( names ) );
    }

    public UserStoreNames add( final UserStoreName... names )
    {
        return add( ImmutableSet.copyOf( names ) );
    }

    public UserStoreNames add( final Iterable<UserStoreName> names )
    {
        return add( ImmutableSet.copyOf( names ) );
    }

    private UserStoreNames add( final ImmutableSet<UserStoreName> names )
    {
        final HashSet<UserStoreName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( names );
        return new UserStoreNames( ImmutableSet.copyOf( tmp ) );
    }

    public UserStoreNames remove( final String... names )
    {
        return remove( parseNames( names ) );
    }

    public UserStoreNames remove( final UserStoreName... names )
    {
        return remove( ImmutableSet.copyOf( names ) );
    }

    public UserStoreNames remove( final Iterable<UserStoreName> names )
    {
        return remove( ImmutableSet.copyOf( names ) );
    }

    private UserStoreNames remove( final ImmutableSet<UserStoreName> names )
    {
        final HashSet<UserStoreName> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( names );
        return new UserStoreNames( ImmutableSet.copyOf( tmp ) );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof UserStoreNames ) && this.set.equals( ( (UserStoreNames) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
    }

    public static UserStoreNames empty()
    {
        final ImmutableSet<UserStoreName> set = ImmutableSet.of();
        return new UserStoreNames( set );
    }

    public static UserStoreNames from( final String... names )
    {
        return new UserStoreNames( parseNames( names ) );
    }

    public static UserStoreNames from( final UserStoreName... names )
    {
        return new UserStoreNames( ImmutableSet.copyOf( names ) );
    }

    public static UserStoreNames from( final Iterable<UserStoreName> names )
    {
        return new UserStoreNames( ImmutableSet.copyOf( names ) );
    }

    private static ImmutableSet<UserStoreName> parseNames( final String... names )
    {
        final Collection<String> list = Lists.newArrayList( names );
        final Collection<UserStoreName> keyList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( keyList );
    }

    private final static class ParseFunction
        implements Function<String, UserStoreName>
    {
        @Override
        public UserStoreName apply( final String value )
        {
            return UserStoreName.from( value );
        }
    }
}
