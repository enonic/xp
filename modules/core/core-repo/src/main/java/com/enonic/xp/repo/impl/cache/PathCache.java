package com.enonic.xp.repo.impl.cache;

import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

public interface PathCache<T>
{
    void cache( CachePath path, BranchDocumentId branchDocumentId );

    void evict( CachePath path );

    void evictAll();

    T get( CachePath path );
}
