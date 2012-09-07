package com.enonic.wem.api.account;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class AccountKeys
    implements Iterable<AccountKey>
{
    private final ImmutableSet<AccountKey> set;

    private AccountKeys( final ImmutableSet<AccountKey> set )
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

    public AccountKeys onlyUsers()
    {
        return filterTypes( AccountType.USER );
    }

    public AccountKeys onlyGroups()
    {
        return filterTypes( AccountType.GROUP );
    }

    public AccountKeys onlyRoles()
    {
        return filterTypes( AccountType.ROLE );
    }

    public AccountKeys filterTypes( final AccountType... types )
    {
        final Collection<AccountKey> list = Collections2.filter( this.set, new TypePredicate( types ) );
        final ImmutableSet<AccountKey> set = ImmutableSet.copyOf( list );
        return new AccountKeys( set );
    }

    public boolean contains( final AccountKey ref )
    {
        return this.set.contains( ref );
    }

    public Set<AccountKey> getSet()
    {
        return this.set;
    }

    @Override
    public Iterator<AccountKey> iterator()
    {
        return this.set.iterator();
    }

    public AccountKeys add( final AccountKeys set )
    {
        final HashSet<AccountKey> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( set.getSet() );

        final ImmutableSet<AccountKey> result = ImmutableSet.copyOf( tmp );
        return new AccountKeys( result );
    }

    public AccountKeys remove( final AccountKeys set )
    {
        final HashSet<AccountKey> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( set.getSet() );

        final ImmutableSet<AccountKey> result = ImmutableSet.copyOf( tmp );
        return new AccountKeys( result );
    }

    public int hashCode()
    {
        return this.set.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof AccountKeys ) && this.set.equals( ( (AccountKeys) o ).set );
    }

    public String toString()
    {
        return this.set.toString();
    }

    public static AccountKeys empty()
    {
        final ImmutableSet<AccountKey> set = ImmutableSet.of();
        return new AccountKeys( set );
    }

    public static AccountKeys from( final String... keys )
    {
        final Collection<String> list = Lists.newArrayList( keys );
        final Collection<AccountKey> keyList = Collections2.transform( list, new ParseFunction() );
        final ImmutableSet<AccountKey> set = ImmutableSet.copyOf( keyList );
        return new AccountKeys( set );
    }

    public static AccountKeys from( final AccountKey... keys )
    {
        final ImmutableSet<AccountKey> set = ImmutableSet.copyOf( keys );
        return new AccountKeys( set );
    }

    public static AccountKeys from( final Iterable<AccountKey> keys )
    {
        final ImmutableSet<AccountKey> set = ImmutableSet.copyOf( keys );
        return new AccountKeys( set );
    }

    private final class TypePredicate
        implements Predicate<AccountKey>
    {
        private final Set<AccountType> types;

        public TypePredicate( final AccountType... types )
        {
            this.types = Sets.newHashSet( types );
        }

        @Override
        public boolean apply( final AccountKey value )
        {
            return this.types.contains( value.getType() );
        }
    }

    private final static class ParseFunction
        implements Function<String, AccountKey>
    {
        @Override
        public AccountKey apply( final String value )
        {
            return AccountKey.from( value );
        }
    }
}
