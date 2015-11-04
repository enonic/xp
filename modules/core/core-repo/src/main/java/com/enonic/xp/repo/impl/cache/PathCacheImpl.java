package com.enonic.xp.repo.impl.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

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
    public void cache( final CachePath path, final BranchDocumentId id )
    {
        this.pathCache.put( path, id.toString() );
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
