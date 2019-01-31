package com.enonic.xp.repo.impl;

import com.enonic.xp.repository.RepositoryId;

public class SingleRepoStorageSource
    implements SearchSource
{
    public enum Type
    {
        BRANCH,
        VERSION,
        COMMIT
    }

    private final RepositoryId repositoryId;

    private final Type type;

    public static SingleRepoStorageSource create( final RepositoryId repositoryId, final Type type )
    {
        return new SingleRepoStorageSource( repositoryId, type );
    }

    public SingleRepoStorageSource( final RepositoryId repositoryId, final Type type )
    {
        this.repositoryId = repositoryId;
        this.type = type;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Type getType()
    {
        return type;
    }
}
