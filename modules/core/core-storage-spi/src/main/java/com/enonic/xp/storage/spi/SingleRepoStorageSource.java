package com.enonic.xp.storage.spi;

import com.enonic.xp.repository.RepositoryId;

public class SingleRepoStorageSource
    implements SearchSource
{
    private final RepositoryId repositoryId;

    private final StaticStoreType type;

    private SingleRepoStorageSource( final RepositoryId repositoryId, final StaticStoreType type )
    {
        this.repositoryId = repositoryId;
        this.type = type;
    }

    public static SingleRepoStorageSource create( final RepositoryId repositoryId, final StaticStoreType type )
    {
        return new SingleRepoStorageSource( repositoryId, type );
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public StaticStoreType getType()
    {
        return type;
    }
}
