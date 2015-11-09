package com.enonic.xp.admin.event.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.event.Event;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class EventListenerImplTest
{
    private EventListenerImpl eventListener;

    private WebSocketManager webSocketManager;

    private BundleEvent bundleEvent;

    private Bundle myBundle;

    @Before
    public final void setUp()
        throws Exception
    {
        bundleEvent = Mockito.mock( BundleEvent.class );
        myBundle = Mockito.mock( Bundle.class );

        Mockito.when( bundleEvent.getType() ).thenReturn( 0x00000001 );
        Mockito.when( myBundle.getSymbolicName() ).thenReturn( "module" );
        Mockito.when( bundleEvent.getBundle() ).thenReturn( myBundle );

        this.webSocketManager = mock( WebSocketManager.class );

        this.eventListener = new EventListenerImpl();
        this.eventListener.setWebSocketManager( this.webSocketManager );
    }

    @Test
    public void testEvent()
        throws Exception
    {
        final ApplicationEvent event = new ApplicationEvent( bundleEvent );
        eventListener.onEvent( event );

        verify( this.webSocketManager, atLeastOnce() ).sendToAll( anyString() );
    }

    @Test
    public void testUnsupportedEvent()
        throws Exception
    {
        final Event event = Mockito.mock( Event.class );
        eventListener.onEvent( event );

        verify( this.webSocketManager, never() ).sendToAll( anyString() );
    }
}
