package com.enonic.xp.repository.internal;

import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryId;

public interface InternalRepositoryService
{
    void initializeRepository( CreateRepositoryParams params );

    boolean isInitialized( RepositoryId id );
}
