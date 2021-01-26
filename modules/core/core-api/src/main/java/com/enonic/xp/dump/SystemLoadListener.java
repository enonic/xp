package com.enonic.xp.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface SystemLoadListener
{
    void totalBranches( long total );

    void loadingBranch( RepositoryId repositoryId, Branch branch, Long total );

    void loadingVersions( RepositoryId repositoryId );

    void loadingCommits( RepositoryId repositoryId );

    void entryLoaded();
}
