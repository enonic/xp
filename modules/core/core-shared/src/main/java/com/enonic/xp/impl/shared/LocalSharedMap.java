package com.enonic.xp.impl.shared;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import com.enonic.xp.shared.SharedMap;

public final class LocalSharedMap<K, V>
    implements SharedMap<K, V>
{
    static Clock clock = Clock.systemUTC();

    private final ConcurrentMap<K, Entry<K, V>> map = new ConcurrentHashMap<>();

    private final DelayQueue<Entry<K, V>> expirationQueue = new DelayQueue<>();

    @Override
    public V get( final K key )
    {
        return Entry.extractValue( map.get( key ) );
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
        if ( value != null )
        {
            map.put( key, newEntry( key, value, ttlSeconds ) );
        }
        else
        {
            map.remove( key );
        }
        cleanUp();
    }

    @Override
    public V modify( final K key, final Function<V, V> modifier )
    {
        return modify( key, modifier, -1 );
    }

    @Override
    public V modify( final K key, final Function<V, V> modifier, final int ttlSeconds )
    {
        final Entry<K, V> computed = map.compute( key, ( k, v ) -> {
            final V value = modifier.apply( Entry.extractValue( v ) );
            if ( value != null )
            {
                return newEntry( k, value, ttlSeconds );
            }
            else
            {
                return null;
            }
        } );
        cleanUp();
        return computed != null ? computed.value : null;
    }

    @Override
    public void removeAll( final Predicate<SharedMap.Entry<K, V>> predicate )
    {
        map.entrySet().removeIf( entry -> {
            final V value = Entry.extractValue( entry.getValue() );
            if ( value != null )
            {
                return predicate.test( new EntryImpl<>( entry.getKey(), value ) );
            }
            return false;
        } );
        cleanUp();
    }

    int cleanUp()
    {
        List<Entry<K, ?>> drainTo = new ArrayList<>();
        final int drained = expirationQueue.drainTo( drainTo );
        if ( drained > 0 )
        {
            drainTo.forEach( holder -> map.remove( holder.key, holder ) );
        }
        return drained;
    }

    private Entry<K, V> newEntry( final K key, final V value, final int ttlSeconds )
    {
        final Entry<K, V> entry = new Entry<>( key, value, ttlSeconds );
        if ( !entry.isPermanent() )
        {
            expirationQueue.add( entry );
        }
        return entry;
    }

    private static class Entry<K, V>
        implements Delayed
    {
        final K key;

        final V value;

        final Instant expires;

        Entry( final K key, final V value, final int ttlSeconds )
        {
            this.key = key;
            this.value = value;
            this.expires = ttlSeconds > 0 ? Instant.now( clock ).plus( ttlSeconds, ChronoUnit.SECONDS ) : Instant.MAX;
        }

        @Override
        public long getDelay( final TimeUnit unit )
        {
            return Instant.now( clock ).until( expires, unit.toChronoUnit() );
        }

        @Override
        public int compareTo( final Delayed o )
        {
            return expires.compareTo( ( (Entry<?, ?>) o ).expires );
        }

        boolean isPermanent()
        {
            return expires.equals( Instant.MAX );
        }

        static <V> V extractValue( final Entry<?, V> entry )
        {
            if ( entry != null && ( entry.isPermanent() || Instant.now( clock ).isBefore( entry.expires ) ) )
            {
                return entry.value;
            }
            else
            {
                return null;
            }
        }
    }

    private static class EntryImpl<K, V>
        implements SharedMap.Entry<K, V>
    {
        private final K key;

        private final V value;

        EntryImpl( final K key, final V value )
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey()
        {
            return key;
        }

        @Override
        public V getValue()
        {
            return value;
        }
    }
}
