package com.enonic.wem.repo.internal.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class PathCacheImpl
    implements PathCache
{
    private final LoadingCache<CachePath, String> pathCache;

    public PathCacheImpl()
    {
        pathCache = CacheBuilder.newBuilder().maximumSize( 10000 ).build( createCacheLoader() );
    }

    private final CacheLoader<CachePath, String> createCacheLoader()
    {
        return new CacheLoader<CachePath, String>()
        {
            @Override
            public String load( final CachePath key )
                throws Exception
            {
                return null;
            }
        };
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
