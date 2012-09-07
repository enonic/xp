package com.enonic.wem.api.account;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

public final class Accounts
    implements Iterable<Account>
{
    private final ImmutableList<Account> list;

    private Accounts( final ImmutableList<Account> list )
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

    public Account getFirst()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public List<Account> getList()
    {
        return this.list;
    }

    public Accounts add( final Account... accounts )
    {
        return add( ImmutableList.copyOf( accounts ) );
    }

    public Accounts add( final Iterable<? extends Account> accounts )
    {
        return add( ImmutableList.copyOf( accounts ) );
    }

    private Accounts add( final ImmutableList<Account> accounts )
    {
        final ImmutableList.Builder<Account> builder = ImmutableList.builder();
        builder.addAll( this.list );
        builder.addAll( accounts );
        return new Accounts( builder.build() );
    }

    public AccountKeys getKeys()
    {
        final Collection<AccountKey> keys = Collections2.transform( this.list, new ToKeyFunction() );
        return AccountKeys.from( keys );
    }

    @Override
    public Iterator<Account> iterator()
    {
        return this.list.iterator();
    }

    public int hashCode()
    {
        return this.list.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof Accounts ) && this.list.equals( ( (Accounts) o ).list );
    }

    public static Accounts empty()
    {
        final ImmutableList<Account> list = ImmutableList.of();
        return new Accounts( list );
    }

    public static Accounts from( final Account... accounts )
    {
        return new Accounts( ImmutableList.copyOf( accounts ) );
    }

    public static Accounts from( final Iterable<? extends Account> accounts )
    {
        return new Accounts( ImmutableList.copyOf( accounts ) );
    }

    public static Accounts from( final Collection<? extends Account> accounts )
    {
        return new Accounts( ImmutableList.copyOf( accounts ) );
    }

    private final static class ToKeyFunction
        implements Function<Account, AccountKey>
    {
        @Override
        public AccountKey apply( final Account value )
        {
            return value.getKey();
        }
    }
}
