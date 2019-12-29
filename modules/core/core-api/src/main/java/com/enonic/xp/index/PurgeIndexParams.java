package com.enonic.xp.index;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
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
