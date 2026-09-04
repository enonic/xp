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

class SystemLoadListenerImplTest
{
    @Test
    void progressAcrossBranches()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        final SystemLoadListenerImpl listener = new SystemLoadListenerImpl( progressReporter );

        listener.totalBranches( 2 );
        listener.loadingBranch( RepositoryId.from( "repo" ), Branch.from( "draft" ), 2L );
        listener.entryLoaded();
        listener.entryLoaded();
        listener.loadingBranch( RepositoryId.from( "repo" ), Branch.from( "master" ), 4L );
        listener.entryLoaded();
        listener.loadingVersions( RepositoryId.from( "repo" ) );
        listener.loadingCommits( RepositoryId.from( "repo" ) );

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
        new SystemLoadListenerImpl( progressReporter ).entryLoaded();

        verify( progressReporter, never() ).progress( any() );
    }
}
