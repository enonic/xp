package com.enonic.wem.api.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class AccountKeySet
    implements Iterable<AccountKey>
{
    private final ImmutableSet<AccountKey> set;

    private AccountKeySet( final Iterable<AccountKey> refs )
    {
        this.set = ImmutableSet.copyOf( refs );
    }

    public int getSize()
    {
        return this.set.size();
    }

    public boolean isEmpty()
    {
        return this.set.isEmpty();
    }

    public AccountKeySet onlyUsers()
    {
        return filterTypes( AccountType.USER );
    }

    public AccountKeySet onlyGroups()
    {
        return filterTypes( AccountType.GROUP );
    }

    public AccountKeySet onlyRoles()
    {
        return filterTypes( AccountType.ROLE );
    }

    public AccountKeySet filterTypes( final AccountType... types )
    {
        final Collection<AccountKey> list = Collections2.filter( this.set, new TypePredicate( types ) );
        return new AccountKeySet( list );
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

    public String toString()
    {
        return this.set.toString();
    }

    public static AccountKeySet from( final String... keys )
    {
        final Collection<String> list = Lists.newArrayList( keys );
        final Collection<AccountKey> keyList = Collections2.transform( list, new ParseFunction() );
        return new AccountKeySet( keyList );
    }

    public static AccountKeySet from( final AccountKey... keys )
    {
        final ArrayList<AccountKey> list = Lists.newArrayList( keys );
        return new AccountKeySet( list );
    }

    public static AccountKeySet from( final Iterable<AccountKey> keys )
    {
        return new AccountKeySet( keys );
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
