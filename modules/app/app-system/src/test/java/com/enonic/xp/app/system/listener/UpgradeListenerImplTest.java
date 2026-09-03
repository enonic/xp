package com.enonic.xp.app.system.listener;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UpgradeListenerImplTest
{
    @Test
    void progressThenFinished()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        final UpgradeListenerImpl listener = new UpgradeListenerImpl( progressReporter );

        listener.total( 3 );
        listener.upgraded();
        listener.upgraded();
        listener.finished();

        final InOrder inOrder = inOrder( progressReporter );
        inOrder.verify( progressReporter ).progress( refEq( ProgressReportParams.create( 1, 3 ).build() ) );
        inOrder.verify( progressReporter ).progress( refEq( ProgressReportParams.create( 2, 3 ).build() ) );
        inOrder.verify( progressReporter ).progress( refEq( ProgressReportParams.create( 3, 3 ).build() ) );
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void finishedWithoutStepsReportsComplete()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        new UpgradeListenerImpl( progressReporter ).finished();

        verify( progressReporter ).progress( refEq( ProgressReportParams.create( 1, 1 ).build() ) );
    }

    @Test
    void finishedAfterAllStepsReportsNothingMore()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        final UpgradeListenerImpl listener = new UpgradeListenerImpl( progressReporter );

        listener.total( 1 );
        listener.upgraded();
        listener.finished();

        verify( progressReporter ).progress( refEq( ProgressReportParams.create( 1, 1 ).build() ) );
    }
}
