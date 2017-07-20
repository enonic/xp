package com.enonic.xp.repo.impl.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

public class BranchCachePath
    implements PathCache<BranchDocumentId>
{
    private final Cache<CachePath, BranchDocumentId> pathCache;

    public BranchCachePath()
    {
        pathCache = CacheBuilder.newBuilder().
            maximumSize( 100000 ).
            build();
    }

    @Override
    public void cache( final CachePath path, final BranchDocumentId id )
    {
        this.pathCache.put( path, id );
    }

    @Override
    public void evict( final CachePath path )
    {
        this.pathCache.invalidate( path );
    }

    @Override
    public void evictAll()
    {
        this.pathCache.invalidateAll();
    }

    @Override
    public BranchDocumentId get( final CachePath path )
    {
        return this.pathCache.getIfPresent( path );
    }
}
