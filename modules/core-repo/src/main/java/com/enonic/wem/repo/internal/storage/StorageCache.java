package com.enonic.wem.repo.internal.storage;

public interface StorageCache
{
    void put( final CacheStoreRequest cacheStoreRequest );

    CacheResult get( final String id );

    CacheResult get( final CacheKey cacheKey );

    void evict( final String id );

}
