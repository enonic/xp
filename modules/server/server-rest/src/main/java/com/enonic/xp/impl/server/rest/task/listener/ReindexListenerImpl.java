package com.enonic.xp.impl.server.rest.task.listener;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.ReindexListener;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

public class ReindexListenerImpl
    implements ReindexListener
{
    private final ProgressReporter progressReporter;

    private int totalBranches = 0;

    private int currentBranchTotal = 0;

    private int currentBranchProgress = 0;

    private int currentBranch = 0;

    public ReindexListenerImpl( final ProgressReporter progressReporter )
    {
        this.progressReporter = progressReporter;
    }

    @Override
    public void totalBranches( final long total )
    {
        this.totalBranches = Math.toIntExact( total );
    }

    @Override
    public void branch( final RepositoryId repositoryId, final Branch branch, final long total )
    {
        currentBranchTotal = Math.toIntExact( total );
        currentBranchProgress = 0;
        currentBranch++;
    }

    @Override
    public void branchEntry( final NodeBranchEntry entry )
    {
        currentBranchProgress++;

        if ( currentBranchTotal != 0 && totalBranches != 0 )
        {
            final int progress = Math.round( 100 * ( ( (float) ( currentBranch - 1 ) / totalBranches ) +
                ( (float) currentBranchProgress / currentBranchTotal / totalBranches ) ) );

            progressReporter.progress( progress, 100 );
        }
    }
}
