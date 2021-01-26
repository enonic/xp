package com.enonic.xp.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface SystemDumpListener
{
    void totalBranches( long total );

    void dumpingBranch( RepositoryId repositoryId, Branch branch, long total );

    void nodeDumped();
}
