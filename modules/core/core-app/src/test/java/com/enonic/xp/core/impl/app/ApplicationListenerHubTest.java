package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationListener;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

class ApplicationListenerHubTest
{
    @Test
    void testActivatedDeactivated()
    {

        final Application app = mock( Application.class );

        ApplicationListenerHub dispatcher = new ApplicationListenerHub();

        final ApplicationListener listener = mock( ApplicationListener.class );
        dispatcher.addListener( listener );

        dispatcher.activated( app );
        dispatcher.deactivated( app );

        final InOrder inOrder = inOrder( listener );

        inOrder.verify( listener, times( 1 ) ).activated( same( app ) );
        inOrder.verify( listener, times( 1 ) ).deactivated( same( app ) );
    }
}
