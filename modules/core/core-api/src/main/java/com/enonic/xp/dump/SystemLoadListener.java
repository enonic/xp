package com.enonic.xp.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface SystemLoadListener
{
    void loadingBranch( final RepositoryId repositoryId, final Branch branch, final Long total );

    void loadingVersions( final RepositoryId repositoryId);

    void entryLoaded();
}
