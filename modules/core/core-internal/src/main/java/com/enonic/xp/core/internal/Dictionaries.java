package com.enonic.xp.core.internal;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

/**
 * Static utility methods pertaining to {@link Dictionary} instances.
 * Not part of public api.
 */
public final class Dictionaries
{
    private Dictionaries()
    {
    }

    /**
     * Returns an unmodifiable {@link Dictionary} containing zero mappings.
     */
    public static <K, V> Dictionary<K, V> of()
    {
        return copyOf( Map.of() );
    }

    /**
     * Returns an unmodifiable {@link Dictionary} containing a single mapping.
     */
    public static <K, V> Dictionary<K, V> of( K k1, V v1 )
    {
        return copyOf( Map.of( k1, v1 ) );
    }

    /**
     * Returns an unmodifiable {@link Dictionary} containing the entries of the given Map.
     * The given Map must not be null, and it must not contain any null keys or values.
     */
    public static <K, V> Dictionary<K, V> copyOf( Map<? extends K, ? extends V> map )
    {
        return new MapAsDictionary<>( Map.copyOf( map ) );
    }

    private static class MapAsDictionary<K, V>
        extends Dictionary<K, V>
    {
        private final Map<K, V> map;

        public MapAsDictionary( final Map<K, V> map )
        {
            this.map = map;
        }

        @Override
        public Enumeration<V> elements()
        {
            return Collections.enumeration( map.values() );
        }

        @Override
        public V get( final Object key )
        {
            return map.get( key );
        }

        @Override
        public boolean isEmpty()
        {
            return map.isEmpty();
        }

        @Override
        public Enumeration<K> keys()
        {
            return Collections.enumeration( map.keySet() );
        }

        @Override
        public V put( final K key, final V value )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove( final Object key )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size()
        {
            return map.size();
        }
    }
}
