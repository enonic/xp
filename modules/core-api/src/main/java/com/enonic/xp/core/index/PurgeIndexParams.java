package com.enonic.xp.core.index;

import com.enonic.xp.core.repository.RepositoryId;

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
