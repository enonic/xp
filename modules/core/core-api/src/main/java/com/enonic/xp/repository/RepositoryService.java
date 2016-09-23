package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public interface RepositoryService
{
    Repository create( final CreateRepositoryParams params );

    Repository get( final RepositoryId repositoryId );

    boolean isInitialized( final RepositoryId id );
}
