package com.enonic.xp.repository.internal;

import com.enonic.xp.repository.RepositoryId;

public interface InternalRepositoryService
{
    void invalidateAll();

    void invalidate( RepositoryId repositoryId );

    void recreateMissing();
}
