package com.enonic.xp.app.system.listener;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ReindexListenerImplTest
{
    @Test
    void progressAcrossBranches()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        final ReindexListenerImpl listener = new ReindexListenerImpl( progressReporter );

        listener.totalBranches( 2 );
        listener.branch( RepositoryId.from( "repo" ), Branch.from( "draft" ), 2 );
        listener.branchEntry();
        listener.branchEntry();
        listener.branch( RepositoryId.from( "repo" ), Branch.from( "master" ), 4 );
        listener.branchEntry();

        final InOrder inOrder = inOrder( progressReporter );
        inOrder.verify( progressReporter ).progress( refEq( ProgressReportParams.create( 25, 100 ).build() ) );
        inOrder.verify( progressReporter ).progress( refEq( ProgressReportParams.create( 50, 100 ).build() ) );
        inOrder.verify( progressReporter ).progress( refEq( ProgressReportParams.create( 63, 100 ).build() ) );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void noProgressWithoutTotals()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        new ReindexListenerImpl( progressReporter ).branchEntry();

        verify( progressReporter, never() ).progress( any() );
    }

    @Test
    void nullReporterIsTolerated()
    {
        final ReindexListenerImpl listener = new ReindexListenerImpl( null );
        listener.totalBranches( 1 );
        listener.branch( RepositoryId.from( "repo" ), Branch.from( "master" ), 1 );
        listener.branchEntry();
    }
}
