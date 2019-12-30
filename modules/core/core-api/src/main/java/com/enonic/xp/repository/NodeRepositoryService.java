package com.enonic.xp.repository;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface NodeRepositoryService
{
    void create( final CreateRepositoryParams params );

    void delete( final RepositoryId repositoryId );

    boolean isInitialized( final RepositoryId repositoryId );

}
