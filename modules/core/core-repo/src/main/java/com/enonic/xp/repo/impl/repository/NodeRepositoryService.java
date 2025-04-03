package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.repository.RepositoryId;

public interface NodeRepositoryService
{
    void create( CreateRepositoryIndexParams params );

    void delete( RepositoryId repositoryId );

    boolean isInitialized( RepositoryId repositoryId );
}
