package com.enonic.xp.repo.impl.cache;

import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

public interface PathCache
{
    void cache( final CachePath path, final BranchDocumentId branchDocumentId );

    void evict( final CachePath path );

    String get( final CachePath path );
}
