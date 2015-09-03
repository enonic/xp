package com.enonic.wem.repo.internal.storage;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.storage.branch.BranchIndexPath;

public class SimpleCache
    implements StorageCache
{
    private final ConcurrentMap<String, StorageData> idCache = Maps.newConcurrentMap();

    private final ConcurrentMap<String, Set<CacheKey>> idCacheKeyMap = Maps.newConcurrentMap();

    private final ConcurrentMap<CacheKey, String> valueCache = Maps.newConcurrentMap();

    private static final Logger LOG = LoggerFactory.getLogger( SimpleCache.class );

    public synchronized void put( final CacheStoreRequest cacheStoreRequest )
    {
        LOG.info( "-------------" );

        doDelete( cacheStoreRequest.getId() );

        LOG.info( "Branch: " + isBranch( cacheStoreRequest ) + " - Insert into cache: " + cacheStoreRequest.getId() );

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

    private boolean isBranch( final CacheStoreRequest cacheStoreRequest )
    {
        final Collection<Object> objects = cacheStoreRequest.getStorageData().getValues().get( BranchIndexPath.BRANCH_NAME.getPath() );
        return objects != null && !objects.isEmpty();
    }

    public CacheResult get( final String id )
    {
        return doGetById( id );
    }

    private CacheResult doGetById( final String id )
    {
        final StorageData storageData = this.idCache.get( id );

        if ( storageData == null )
        {
            LOG.info( "CacheMiss: " + id );
        }
        else
        {
            LOG.info( "CacheHit: " + id );
        }

        return new CacheResult( storageData, id );
    }

    public CacheResult get( final CacheKey cacheKey )
    {
        final String id = valueCache.get( cacheKey );

        if ( id == null )
        {
            return CacheResult.empty();
        }

        return doGetById( id );
    }

    public void evict( final String id )
    {
        doDelete( id );
    }

    public void evict( final CacheKey cacheKey )
    {
        final String id = valueCache.get( cacheKey );

        if ( id == null )
        {
            LOG.info( "Evict cachekey miss: " + id );
            return;
        }
        else
        {
            LOG.info( "Evict cachekey hit: " + id );
        }

        final Set<CacheKey> cacheKeys = this.idCacheKeyMap.get( id );

        this.idCache.remove( id );
        this.idCacheKeyMap.remove( id );

        for ( final CacheKey key : cacheKeys )
        {
            this.valueCache.remove( key );
        }
    }

    private synchronized void doDelete( final String id )
    {
        final StorageData remove = idCache.remove( id );

        if ( remove == null )
        {
            LOG.info( "DELETE MISS: " + id );
        }
        else
        {
            LOG.info( "DELETE HIT: " + id );
        }

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
