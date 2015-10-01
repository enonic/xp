package com.enonic.wem.repo.internal.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class PathCacheImpl
    implements PathCache
{
    private final Cache<CachePath, String> pathCache;

    public PathCacheImpl()
    {
        pathCache = CacheBuilder.newBuilder().
            maximumSize( 10000 ).
            build();
    }

    @Override
    public void cache( final CachePath path, final String id )
    {
        this.pathCache.put( path, id );
    }

    @Override
    public void evict( final CachePath path )
    {
        this.pathCache.invalidate( path );
    }

    @Override
    public String get( final CachePath path )
    {
        return this.pathCache.getIfPresent( path );
    }
}
