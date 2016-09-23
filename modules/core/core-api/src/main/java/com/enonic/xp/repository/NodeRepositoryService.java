package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public interface NodeRepositoryService
{
    Repository create( final CreateRepositoryParams params );

    boolean isInitialized( final RepositoryId repositoryId );

}
