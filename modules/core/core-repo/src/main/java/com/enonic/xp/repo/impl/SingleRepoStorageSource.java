package com.enonic.xp.repo.impl;

import com.enonic.xp.repository.RepositoryId;

public class SingleRepoStorageSource
    implements DataSource
{
    public enum Type
    {
        BRANCH,
        VERSION
    }

    private final RepositoryId repositoryId;

    private final Type type;

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
