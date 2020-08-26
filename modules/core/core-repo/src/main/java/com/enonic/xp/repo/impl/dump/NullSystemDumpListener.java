package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.repository.RepositoryId;

public class NullSystemDumpListener
    implements SystemDumpListener
{

    @Override
    public void totalBranches( final long total )
    {

    }

    @Override
    public void dumpingBranch( final RepositoryId repositoryId, final Branch branch, final long total )
    {
    }

    @Override
    public void nodeDumped()
    {

    }
}
