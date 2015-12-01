package com.enonic.xp.admin.event.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.Event2;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class EventListenerImplTest
{
    private EventListenerImpl eventListener;

    private WebSocketManager webSocketManager;

    @Before
    public final void setUp()
        throws Exception
    {
        this.webSocketManager = mock( WebSocketManager.class );

        this.eventListener = new EventListenerImpl();
        this.eventListener.setWebSocketManager( this.webSocketManager );
    }

    @Test
    public void testEvent()
        throws Exception
    {
        final Event2 event2 = Event2.create( "application" ).
            distributed( false ).
            value( "applicationKey", "module" ).
            value( "eventType", BundleEvent.INSTALLED ).
            build();
        eventListener.onEvent( event2 );

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
