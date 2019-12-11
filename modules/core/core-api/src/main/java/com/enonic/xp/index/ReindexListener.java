package com.enonic.xp.index;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.repository.RepositoryId;

public interface ReindexListener
{
    void totalBranches( final long total );

    void branch( final RepositoryId repoId, final Branch branch, final long total );

    void branchEntry( final NodeBranchEntry entry );
}
