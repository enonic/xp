package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public interface NodeRepositoryService
{
    RepositoryId create( final RepositorySettings repositorySettings );

    boolean isInitialized( final RepositoryId repositoryId );

}
