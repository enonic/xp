package com.enonic.xp.security;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class IdProviders
    extends AbstractImmutableEntityList<IdProvider>
{
    private static final IdProviders EMPTY = new IdProviders( ImmutableList.of() );

    private IdProviders( final ImmutableList<IdProvider> list )
    {
        super( list );
    }

    public static IdProviders empty()
    {
        return EMPTY;
    }

    public static IdProviders from( final IdProvider... idProviders )
    {
        return fromInternal( ImmutableList.copyOf( idProviders ) );
    }

    public static IdProviders from( final Iterable<IdProvider> idProviders )
    {
        return idProviders instanceof IdProviders i ? i : fromInternal( ImmutableList.copyOf( idProviders ) );
    }

    public static Collector<IdProvider, ?, IdProviders> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), IdProviders::fromInternal );
    }

    private static IdProviders fromInternal( final ImmutableList<IdProvider> list )
    {
        return list.isEmpty() ? EMPTY : new IdProviders( list );
    }

    public IdProviderKeys getKeys()
    {
        return list.stream().map( IdProvider::getKey ).collect( IdProviderKeys.collector() );
    }

    public IdProvider getIdProvider( final IdProviderKey idProviderKey )
    {
        return list.stream().filter( idp -> idProviderKey.equals( idp.getKey() ) ).findFirst().orElse( null );
    }
}
