package com.enonic.xp.repository;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface NodeRepositoryService
{
    void create( CreateRepositoryParams params );

    void delete( RepositoryId repositoryId );

    boolean isInitialized( RepositoryId repositoryId );
}
