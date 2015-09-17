package com.enonic.xp.index;

import com.google.common.annotations.Beta;

import com.enonic.xp.repository.RepositoryId;

@Beta
public class PurgeIndexParams
{

    private final RepositoryId repositoryId;


    public PurgeIndexParams( final RepositoryId repositoryId )
    {
        this.repositoryId = repositoryId;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }
}
