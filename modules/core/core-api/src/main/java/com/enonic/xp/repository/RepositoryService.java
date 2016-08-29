package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public interface RepositoryService
{
    RepositoryId create( final RepositorySettings repositorySettings );

    boolean isInitialized( final RepositoryId repositoryId );

}
