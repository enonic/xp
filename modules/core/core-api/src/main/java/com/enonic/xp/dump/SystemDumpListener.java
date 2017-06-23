package com.enonic.xp.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;

public interface SystemDumpListener
{
    void dumpingBranch( final RepositoryId repositoryId, final Branch branch, final long total );

    void nodeDumped();
}
