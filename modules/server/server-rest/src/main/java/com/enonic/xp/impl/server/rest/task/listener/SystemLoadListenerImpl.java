package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

public class SystemLoadListenerImpl
    implements SystemLoadListener
{
    private final ProgressReporter progressReporter;

    private int total = 0;

    private int currentBranchTotal = 0;

    private int currentBranchProgress = 0;

    private int currentBranch = 0;

    public SystemLoadListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void totalBranches( final long total )
    {
        this.total = Math.toIntExact( total );
    }

    @Override
    public void loadingBranch( final RepositoryId repositoryId, final Branch branch, final Long total )
    {
        currentBranchTotal = Math.toIntExact( total );
        currentBranchProgress = 0;
        currentBranch++;
    }

    @Override
    public void entryLoaded()
    {
        currentBranchProgress++;

        if ( currentBranchTotal != 0 && total != 0 )
        {
            final int progress = Math.round(
                100 * ( ( (float) ( currentBranch - 1 ) / total ) + ( (float) currentBranchProgress / currentBranchTotal / total ) ) );

            progressReporter.progress( progress, 100 );
        }
    }

    @Override
    public void loadingVersions( final RepositoryId repositoryId )
    {
    }
}
