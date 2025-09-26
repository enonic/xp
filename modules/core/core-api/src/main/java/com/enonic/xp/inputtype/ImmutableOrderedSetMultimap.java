package com.enonic.xp.inputtype;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class ImmutableOrderedSetMultimap<K, V>
{
    private final Map<K, Set<V>> map;

    private ImmutableOrderedSetMultimap( Map<K, Set<V>> map )
    {
        this.map = map;
    }

    public Set<V> get( K key )
    {
        return map.getOrDefault( key, Collections.emptySet() );
    }

    public Set<K> keySet()
    {
        return map.keySet();
    }

    public static <K, V> Builder<K, V> builder()
    {
        return new Builder<>();
    }

    public static class Builder<K, V>
    {
        private final LinkedHashMap<K, LinkedHashSet<V>> map = new LinkedHashMap<>();

        public Builder<K, V> put( K key, V value )
        {
            map.computeIfAbsent( key, k -> new LinkedHashSet<>() ).add( value );
            return this;
        }

        public Builder<K, V> putAll( K key, Collection<? extends V> values )
        {
            map.computeIfAbsent( key, k -> new LinkedHashSet<>() ).addAll( values );
            return this;
        }

        public Builder<K, V> putAll( ImmutableOrderedSetMultimap<K, V> other )
        {
            for ( K key : other.keySet() )
            {
                putAll( key, other.get( key ) );
            }
            return this;
        }

        public ImmutableOrderedSetMultimap<K, V> build()
        {
            final Map<K, Set<V>> result = new LinkedHashMap<>();
            for ( Map.Entry<K, LinkedHashSet<V>> entry : map.entrySet() )
            {
                result.put( entry.getKey(), Collections.unmodifiableSet( new LinkedHashSet<>( entry.getValue() ) ) );
            }
            return new ImmutableOrderedSetMultimap<>( Collections.unmodifiableMap( result ) );
        }
    }
}
