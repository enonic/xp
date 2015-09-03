package com.enonic.wem.repo.internal.storage;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AnotherCache
{
    private final ConcurrentMap<String, StorageData> idCache = Maps.newConcurrentMap();

    private final ConcurrentMap<String, Set<CacheKey>> idCacheKeyMap = Maps.newConcurrentMap();

    private final ConcurrentMap<CacheKey, String> valueCache = Maps.newConcurrentMap();

    public void store( final CacheStoreRequest cacheStoreRequest )
    {
        doDelete( cacheStoreRequest.getId() );

        if ( cacheStoreRequest.getStorageData() == null )
        {
            return;
        }

        final String id = cacheStoreRequest.getId();

        idCache.put( id, cacheStoreRequest.getStorageData() );

        for ( final CacheKey key : cacheStoreRequest.getCacheKeys() )
        {
            valueCache.put( key, id );
            Set<CacheKey> cacheKeys = idCacheKeyMap.get( id );

            if ( cacheKeys == null )
            {
                cacheKeys = Sets.newHashSet();
                idCacheKeyMap.put( id, cacheKeys );
            }

            cacheKeys.add( key );
        }
    }

    public CacheResult get( final String id )
    {
        return new CacheResult( this.idCache.get( id ), id );
    }

    public CacheResult get( final CacheKey cacheKey )
    {
        final String id = valueCache.get( cacheKey );

        if ( id == null )
        {
            return CacheResult.empty();
        }

        return new CacheResult( this.idCache.get( id ), id );
    }

    public void delete( final String id )
    {
        doDelete( id );
    }

    private void doDelete( final String id )
    {
        idCache.remove( id );

        final Set<CacheKey> cacheKeys = idCacheKeyMap.get( id );

        if ( cacheKeys != null )
        {
            for ( final CacheKey key : cacheKeys )
            {
                this.valueCache.remove( key );
            }
        }

        idCacheKeyMap.remove( id );
    }

}
