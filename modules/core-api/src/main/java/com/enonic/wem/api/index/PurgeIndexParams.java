package com.enonic.wem.api.index;

import com.enonic.wem.api.repository.RepositoryId;

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
