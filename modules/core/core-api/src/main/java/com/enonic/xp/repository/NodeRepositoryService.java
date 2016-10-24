package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

@Beta
public interface NodeRepositoryService
{
    void create( final CreateRepositoryParams params );

    void delete( final RepositoryId repositoryId );

    boolean isInitialized( final RepositoryId repositoryId );

}
