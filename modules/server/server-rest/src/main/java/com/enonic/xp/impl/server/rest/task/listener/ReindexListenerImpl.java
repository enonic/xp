package com.enonic.xp.impl.server.rest.task.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.ReindexListener;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;

public class ReindexListenerImpl
    implements ReindexListener
{
    private final static Logger LOG = LoggerFactory.getLogger( ReindexListenerImpl.class );

    private final ProgressReporter progressReporter;

    private int totalBranches = 0;

    private int currentBranchTotal = 0;

    private int currentBranchProgress = 0;

    private int currentBranch = 0;

    private int logStep = 1;

    public ReindexListenerImpl()
    {
        this.progressReporter = null;
    }

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
        LOG.info( "Reindexing branch [" + branch + "] in repository [" + repositoryId + "]" );
        currentBranchTotal = Math.toIntExact( total );
        currentBranchProgress = 0;
        currentBranch++;
        logStep = total < 10 ? 1 : total < 100 ? 10 : total < 1000 ? 100 : 1000;
    }

    @Override
    public void branchEntry( final NodeBranchEntry entry )
    {
        currentBranchProgress++;

        if ( currentBranchProgress % logStep == 0 )
        {
            LOG.info( "Branch reindex progress: " + currentBranchProgress + "/" + currentBranchTotal );
        }

        if ( progressReporter != null && currentBranchTotal != 0 && totalBranches != 0 )
        {
            final int progress = Math.round( 100 * ( ( (float) ( currentBranch - 1 ) / totalBranches ) +
                ( (float) currentBranchProgress / currentBranchTotal / totalBranches ) ) );

            progressReporter.progress( progress, 100 );
        }
    }
}
