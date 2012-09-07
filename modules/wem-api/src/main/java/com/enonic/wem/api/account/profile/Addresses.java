package com.enonic.wem.api.account.profile;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class Addresses
    implements Iterable<Address>
{
    private final ImmutableList<Address> list;

    private Addresses( final ImmutableList<Address> list )
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

    public Address getPrimary()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public List<Address> getList()
    {
        return this.list;
    }

    public Addresses add( final Address... list )
    {
        return add( ImmutableList.copyOf( list ) );
    }

    public Addresses add( final Iterable<Address> list )
    {
        return add( ImmutableList.copyOf( list ) );
    }

    private Addresses add( final ImmutableList<Address> list )
    {
        final ImmutableList.Builder<Address> builder = ImmutableList.builder();
        builder.addAll( this.list );
        builder.addAll( list );
        return new Addresses( builder.build() );
    }

    @Override
    public Iterator<Address> iterator()
    {
        return this.list.iterator();
    }

    public static Addresses empty()
    {
        final ImmutableList<Address> list = ImmutableList.of();
        return new Addresses( list );
    }

    public static Addresses from( final Address... list )
    {
        return new Addresses( ImmutableList.copyOf( list ) );
    }

    public static Addresses from( final Iterable<Address> list )
    {
        return new Addresses( ImmutableList.copyOf( list ) );
    }
}
