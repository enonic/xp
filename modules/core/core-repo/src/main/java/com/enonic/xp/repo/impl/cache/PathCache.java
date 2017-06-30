package com.enonic.xp.repo.impl.cache;

import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

public interface PathCache<T>
{
    void cache( final CachePath path, final BranchDocumentId branchDocumentId );

    void evict( final CachePath path );

    void evictAll();

    T get( final CachePath path );
}
