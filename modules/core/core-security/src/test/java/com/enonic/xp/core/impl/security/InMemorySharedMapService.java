package com.enonic.xp.core.impl.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

/**
 * A single-process stand-in for the cluster map: enough to test key creation and lookup.
 */
public final class InMemorySharedMapService
    implements SharedMapService
{
    private final Map<String, SharedMap<?, ?>> maps = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> SharedMap<K, V> getSharedMap( final String name )
    {
        return (SharedMap<K, V>) maps.computeIfAbsent( name, n -> new InMemorySharedMap<>() );
    }

    private static final class InMemorySharedMap<K, V>
        implements SharedMap<K, V>
    {
        private final Map<K, V> map = new ConcurrentHashMap<>();

        @Override
        public V get( final K key )
        {
            return map.get( key );
        }

        @Override
        public void delete( final K key )
        {
            map.remove( key );
        }

        @Override
        public void set( final K key, final V value )
        {
            set( key, value, -1 );
        }

        @Override
        public void set( final K key, final V value, final int ttlSeconds )
        {
            if ( value == null )
            {
                map.remove( key );
            }
            else
            {
                map.put( key, value );
            }
        }

        @Override
        public V modify( final K key, final Function<V, V> modifier )
        {
            return modify( key, modifier, -1 );
        }

        @Override
        public V modify( final K key, final Function<V, V> modifier, final int ttlSeconds )
        {
            return map.compute( key, ( k, v ) -> modifier.apply( v ) );
        }
    }
}
