package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.repository.RepositoryId;

public class NullSystemLoadListener
    implements SystemLoadListener
{
    @Override
    public void totalBranches( final long total )
    {

    }

    @Override
    public void loadingBranch( final RepositoryId repositoryId, final Branch branch, final Long total )
    {

    }

    @Override
    public void loadingVersions( final RepositoryId repositoryId )
    {

    }

    @Override
    public void loadingCommits( final RepositoryId repositoryId )
    {

    }

    @Override
    public void entryLoaded()
    {

    }
}
