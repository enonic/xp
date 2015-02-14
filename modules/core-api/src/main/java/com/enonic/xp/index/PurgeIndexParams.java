package com.enonic.xp.index;

import com.enonic.xp.repository.RepositoryId;

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
