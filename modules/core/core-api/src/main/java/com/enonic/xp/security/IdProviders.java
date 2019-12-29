package com.enonic.xp.security;

import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class IdProviders
    extends AbstractImmutableEntityList<IdProvider>
{
    private final ImmutableMap<IdProviderKey, IdProvider> map;

    private IdProviders( final ImmutableList<IdProvider> list )
    {
        super( list );
        this.map = list.stream().collect( ImmutableMap.toImmutableMap( IdProvider::getKey, Function.identity() ) );
    }

    public static IdProviders empty()
    {
        return new IdProviders( ImmutableList.of() );
    }

    public static IdProviders from( final IdProvider... idProviders )
    {
        return new IdProviders( ImmutableList.copyOf( idProviders ) );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static IdProviders from( final Iterable<? extends IdProvider> idProviders )
    {
        return new IdProviders( ImmutableList.copyOf( idProviders ) );
    }

    public IdProviderKeys getKeys()
    {
        return IdProviderKeys.from( map.keySet() );
    }

    public IdProvider getIdProvider( final IdProviderKey idProviderKey )
    {
        return map.get( idProviderKey );
    }

}
