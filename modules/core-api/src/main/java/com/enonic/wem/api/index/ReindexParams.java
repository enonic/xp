package com.enonic.wem.api.index;

import com.enonic.wem.api.repository.RepositoryId;

public class ReindexParams
{
    private boolean initialize;

    private RepositoryId repositoryId;

    private IndexType[] indexTypes;

    public boolean isInitialize()
    {
        return initialize;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public IndexType[] getIndexTypes()
    {
        return indexTypes;
    }
}


