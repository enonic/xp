package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public interface RepositoryService
{
    RepositoryId createRepository( final RepositorySettings repositorySettings );

    Repository getRepository( final RepositoryId repositoryId );

    boolean isInitialized( final RepositoryId id );
}
