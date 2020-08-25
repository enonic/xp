package com.enonic.xp.admin.event.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleEvent;

import com.enonic.xp.event.Event;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WebsocketEventListenerTest
{
    private WebsocketEventListener eventListener;

    private WebsocketManager webSocketManager;

    @BeforeEach
    public final void setUp()
        throws Exception
    {
        this.webSocketManager = mock( WebsocketManager.class );

        this.eventListener = new WebsocketEventListener( this.webSocketManager );
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
