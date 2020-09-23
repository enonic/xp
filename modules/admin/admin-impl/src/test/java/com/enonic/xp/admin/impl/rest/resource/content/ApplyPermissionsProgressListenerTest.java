package com.enonic.xp.admin.impl.rest.resource.content;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.enonic.xp.task.ProgressReporter;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

class ApplyPermissionsProgressListenerTest
{
    @Test
    void test()
    {
        final ProgressReporter progressReporter = mock( ProgressReporter.class );
        final ApplyPermissionsProgressListener listener = new ApplyPermissionsProgressListener( progressReporter );

        listener.setTotal( Integer.MAX_VALUE );

        listener.permissionsApplied( 1 );
        listener.notEnoughRights( 2 );
        listener.permissionsApplied( 3 );
        listener.notEnoughRights( 4 );

        final InOrder inOrder = inOrder( progressReporter );
        inOrder.verify( progressReporter ).progress( 1, Integer.MAX_VALUE );
        inOrder.verify( progressReporter ).progress( 3, Integer.MAX_VALUE );
        inOrder.verify( progressReporter ).progress( 6, Integer.MAX_VALUE );
        inOrder.verify( progressReporter ).progress( 10, Integer.MAX_VALUE );
        inOrder.verifyNoMoreInteractions();
    }
}
