package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplicationListenerHubTest
{
    @Test
    void testActivatedDeactivated()
    {

        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        ApplicationListenerHub dispatcher = new ApplicationListenerHub();

        final ApplicationListener listener = mock( ApplicationListener.class );
        dispatcher.addListener( listener );

        dispatcher.activated( app );
        dispatcher.deactivated( app );

        final InOrder inOrder = inOrder( listener );

        inOrder.verify( listener, times( 1 ) ).activated( same( app ) );
        inOrder.verify( listener, times( 1 ) ).deactivated( same( app ) );
    }

    @Test
    void testLateListenerReplaysActivation()
    {
        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        final ApplicationListenerHub dispatcher = new ApplicationListenerHub();

        dispatcher.activated( app );

        final ApplicationListener listener = mock( ApplicationListener.class );
        dispatcher.addListener( listener );

        verify( listener, times( 1 ) ).activated( same( app ) );
    }

    @Test
    void testDeactivatedApplicationIsNotReplayed()
    {
        final Application app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( "foo.bar" ) );

        final ApplicationListenerHub dispatcher = new ApplicationListenerHub();

        dispatcher.activated( app );
        dispatcher.deactivated( app );

        final ApplicationListener listener = mock( ApplicationListener.class );
        dispatcher.addListener( listener );

        verify( listener, never() ).activated( same( app ) );
        verify( listener, never() ).deactivated( same( app ) );
    }

    @Test
    void testMultipleActiveApplicationsReplayedInOrder()
    {
        final Application app1 = mock( Application.class );
        when( app1.getKey() ).thenReturn( ApplicationKey.from( "foo.one" ) );

        final Application app2 = mock( Application.class );
        when( app2.getKey() ).thenReturn( ApplicationKey.from( "foo.two" ) );

        final ApplicationListenerHub dispatcher = new ApplicationListenerHub();

        dispatcher.activated( app1 );
        dispatcher.activated( app2 );

        final ApplicationListener listener = mock( ApplicationListener.class );
        dispatcher.addListener( listener );

        final InOrder inOrder = inOrder( listener );
        inOrder.verify( listener, times( 1 ) ).activated( same( app1 ) );
        inOrder.verify( listener, times( 1 ) ).activated( same( app2 ) );
    }
}
