package com.enonic.xp.admin.event.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.event.Event;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EventListenerImplTest
{
    private EventListenerImpl eventListener;

    private WebSocketManager webSocketManager;

    @BeforeEach
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
        final Event event = Event.create( "application" ).
            distributed( false ).
            value( "applicationKey", "module" ).
            value( "eventType", BundleEvent.INSTALLED ).
            build();
        eventListener.onEvent( event );

        verify( this.webSocketManager, atLeastOnce() ).sendToAll( anyString() );
    }
}
