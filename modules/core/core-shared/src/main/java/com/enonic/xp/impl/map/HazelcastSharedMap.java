package com.enonic.xp.impl.map;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.hazelcast.core.IMap;

import com.enonic.xp.shared.SharedMap;

public final class HazelcastSharedMap<K, V>
    implements SharedMap<K, V>
{
    private final IMap<K, V> map;

    public HazelcastSharedMap( final IMap<K, V> map )
    {
        this.map = map;
    }

    @Override
    public V get( final K key )
    {
        return map.get( key );
    }

    @Override
    public void delete( final K key )
    {
        map.delete( key );
    }

    @Override
    public void set( final K key, final V value )
    {
        this.set( key, value, -1 );
    }

    @Override
    public void set( final K key, final V value, int ttlSeconds )
    {
        setInternal( key, value, ttlSeconds );
    }

    @Override
    public V modify( K key, Function<V, V> callback )
    {
        return modify( key, callback, -1 );
    }

    @Override
    public V modify( K key, Function<V, V> modifier, int ttlSeconds )
    {
        map.lock( key );
        try
        {
            final V value = modifier.apply( map.get( key ) );
            setInternal( key, value, ttlSeconds );
            return value;
        }
        finally
        {
            map.unlock( key );
        }
    }

    private void setInternal( final K key, final V value, final int ttlSeconds )
    {
        if ( value != null )
        {
            map.set( key, value, ttlSeconds, TimeUnit.SECONDS );
        }
        else
        {
            map.delete( key );
        }
    }
}
