package com.enonic.xp.inputtype;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.Set;

public record ObjectPropertyValue(SequencedMap<String, PropertyValue> value)
    implements PropertyValue
{
    public ObjectPropertyValue( final SequencedMap<String, PropertyValue> value )
    {
        this.value = unmodifiableSequencedMap( Objects.requireNonNull( value ) );
    }

    @Override
    public Set<Map.Entry<String, PropertyValue>> getProperties()
    {
        return value.sequencedEntrySet();
    }

    private static <K, V> SequencedMap<K, V> unmodifiableSequencedMap( final SequencedMap<K, V> map )
    {
        return new SequencedMap<>()
        {
            @Override
            public V get( Object key )
            {
                return map.get( key );
            }

            @Override
            public int size()
            {
                return map.size();
            }

            @Override
            public boolean isEmpty()
            {
                return map.isEmpty();
            }

            @Override
            public boolean containsKey( Object key )
            {
                return map.containsKey( key );
            }

            @Override
            public boolean containsValue( Object value )
            {
                return map.containsValue( value );
            }

            @Override
            public V put( K key, V value )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public V remove( Object key )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void putAll( Map<? extends K, ? extends V> m )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set<K> keySet()
            {
                return Collections.unmodifiableSet( map.keySet() );
            }

            @Override
            public Collection<V> values()
            {
                return Collections.unmodifiableCollection( map.values() );
            }

            @Override
            public Set<Map.Entry<K, V>> entrySet()
            {
                return Collections.unmodifiableSet( map.entrySet() );
            }

            @Override
            public Map.Entry<K, V> firstEntry()
            {
                return map.firstEntry();
            }

            @Override
            public Map.Entry<K, V> lastEntry()
            {
                return map.lastEntry();
            }

            @Override
            public SequencedMap<K, V> reversed()
            {
                return unmodifiableSequencedMap( map.reversed() );
            }
        };
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}
