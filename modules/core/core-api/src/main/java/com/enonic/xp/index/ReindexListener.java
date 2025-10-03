package com.enonic.xp.index;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface ReindexListener
{
    void totalBranches( long total );

    void branch( RepositoryId repoId, Branch branch, long total );

    void branchEntry();
}
