package com.enonic.xp.impl.server.rest.task.listener;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.enonic.xp.task.ProgressReporter;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class ExportListenerImplTest
{
    @Test
    void test()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        final ExportListenerImpl listener = new ExportListenerImpl( progressReporter );

        listener.nodeResolved( Integer.MAX_VALUE );

        listener.nodeExported( 1 );
        listener.nodeExported( 2 );

        final InOrder inOrder = inOrder( progressReporter );
        inOrder.verify( progressReporter ).progress( 1, Integer.MAX_VALUE );
        inOrder.verify( progressReporter ).progress( 3, Integer.MAX_VALUE );
        inOrder.verifyNoMoreInteractions();
    }
}
