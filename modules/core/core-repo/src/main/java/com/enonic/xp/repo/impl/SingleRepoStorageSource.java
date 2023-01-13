package com.enonic.xp.repo.impl;

import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repository.RepositoryId;

public class SingleRepoStorageSource
    implements SearchSource
{
    private final RepositoryId repositoryId;

    private final StaticStorageType type;

    private SingleRepoStorageSource( final RepositoryId repositoryId, final StaticStorageType type )
    {
        this.repositoryId = repositoryId;
        this.type = type;
    }

    public static SingleRepoStorageSource create( final RepositoryId repositoryId, final StaticStorageType type )
    {
        return new SingleRepoStorageSource( repositoryId, type );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public StaticStorageType getType()
    {
        return type;
    }
}
