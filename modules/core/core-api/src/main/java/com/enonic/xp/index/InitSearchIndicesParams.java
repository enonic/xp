package com.enonic.xp.index;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;

@Beta
public class InitSearchIndicesParams
{
    private final RepositoryId repositoryId;

    private final Branches branches;

    public InitSearchIndicesParams( final RepositoryId repositoryId, final Branches branches )
    {
        this.repositoryId = repositoryId;
        this.branches = branches;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public Branches getBranches()
    {
        return branches;
    }
}
