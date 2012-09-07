package com.enonic.wem.api.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public final class Accounts
    implements Iterable<Account>
{
    private final ArrayList<Account> list;

    private Accounts()
    {
        this.list = Lists.newArrayList();
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
        return ImmutableList.copyOf( this.list );
    }

    public Accounts add( final Account... accounts )
    {
        return add( Lists.newArrayList( accounts ) );
    }

    public Accounts add( final Iterable<? extends Account> accounts )
    {
        return add( Lists.newArrayList( accounts ) );
    }

    public Accounts add( final Collection<? extends Account> accounts )
    {
        this.list.addAll( accounts );
        return this;
    }

    @Override
    public Iterator<Account> iterator()
    {
        return Iterators.unmodifiableIterator( this.list.iterator() );
    }

    public Accounts copy()
    {
        final Accounts result = new Accounts();
        result.list.addAll( this.list );
        return result;
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
        return new Accounts();
    }

    public static Accounts from( final Account... accounts )
    {
        return empty().add( accounts );
    }

    public static Accounts from( final Iterable<? extends Account> accounts )
    {
        return empty().add( accounts );
    }

    public static Accounts from( final Collection<? extends Account> accounts )
    {
        return empty().add( accounts );
    }
}
