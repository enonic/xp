package com.enonic.xp.security;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class IdProviders
    extends AbstractImmutableEntityList<IdProvider>
{
    private final ImmutableMap<IdProviderKey, IdProvider> map;

    private IdProviders( final ImmutableList<IdProvider> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, IdProvider::getKey );
    }

    public static IdProviders empty()
    {
        final ImmutableList<IdProvider> list = ImmutableList.of();
        return new IdProviders( list );
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

    public IdProvider getIdProvider( final IdProviderKey IdProviderKey )
    {
        return map.get( IdProviderKey );
    }

}
