package com.enonic.xp.repository;

import com.enonic.xp.index.IndexType;

public interface RepositoryService
{
    Repository create( final RepositorySettings repositorySettings );

    void createIndex( final RepositoryId repositoryId, final IndexType indexType, final IndexConfig indexConfig );
}
