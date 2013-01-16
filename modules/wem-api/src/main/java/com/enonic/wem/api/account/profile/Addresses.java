package com.enonic.wem.api.account.profile;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.util.AbstractImmutableEntityList;

public final class Addresses
    extends AbstractImmutableEntityList<Address>
{
    private Addresses( final ImmutableList<Address> list )
    {
        super( list );
    }

    public Address getPrimary()
    {
        return first();
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
