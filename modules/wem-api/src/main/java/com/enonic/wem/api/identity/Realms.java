package com.enonic.wem.api.identity;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class Realms
    extends AbstractImmutableEntityList<Realm>
{
    private final ImmutableMap<RealmKey, Realm> map;

    private Realms( final ImmutableList<Realm> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, Realm::getKey );
    }

    public RealmKeys getKeys()
    {
        return RealmKeys.from( map.keySet() );
    }

    public Realm getRealm( final RealmKey RealmKey )
    {
        return map.get( RealmKey );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Realms empty()
    {
        final ImmutableList<Realm> list = ImmutableList.of();
        return new Realms( list );
    }

    public static Realms from( final Realm... realms )
    {
        return new Realms( ImmutableList.copyOf( realms ) );
    }

    public static Realms from( final Iterable<? extends Realm> realms )
    {
        return new Realms( ImmutableList.copyOf( realms ) );
    }

    public static Realms from( final Collection<? extends Realm> realms )
    {
        return new Realms( ImmutableList.copyOf( realms ) );
    }

}
