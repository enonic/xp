package com.enonic.wem.core.hazelcast.store;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hazelcast.core.MapStore;

public abstract class StringBasedMapStore<K, V>
    implements MapStore<K, V>
{
    @Override
    public final void store( final K key, final V value )
    {
        final String strKey = keyToString( key );
        final String strValue = valueToString( value );
        doStore( strKey, strValue );
    }

    @Override
    public final void storeAll( final Map<K, V> map )
    {
        for ( final Map.Entry<K, V> entry : map.entrySet() )
        {
            store( entry.getKey(), entry.getValue() );
        }
    }

    @Override
    public final void delete( final K key )
    {
        final String strKey = keyToString( key );
        doDelete( strKey );
    }

    @Override
    public final void deleteAll( final Collection<K> keys )
    {
        for ( final K key : keys )
        {
            delete( key );
        }
    }

    @Override
    public final V load( final K key )
    {
        final String strKey = keyToString( key );
        final String strValue = doLoad( strKey );

        if ( strValue == null )
        {
            return null;
        }

        return stringToValue( strValue );
    }

    @Override
    public final Map<K, V> loadAll( final Collection<K> keys )
    {
        final Map<K, V> map = Maps.newHashMap();

        for ( final K key : keys )
        {
            final V value = load( key );
            if ( value != null )
            {
                map.put( key, value );
            }
        }

        return map;
    }

    @Override
    public final Set<K> loadAllKeys()
    {
        final Set<K> keys = Sets.newHashSet();
        final Set<String> strKeys = doLoadAllKeys();

        for ( final String strKey : strKeys )
        {
            keys.add( stringToKey( strKey ) );
        }

        return keys;
    }

    protected abstract String keyToString( K key );

    protected abstract String valueToString( V value );

    protected abstract K stringToKey( String str );

    protected abstract V stringToValue( String str );

    protected abstract void doStore( final String key, final String value );

    protected abstract void doDelete( final String key );

    protected abstract String doLoad( final String key );

    protected abstract Set<String> doLoadAllKeys();
}
