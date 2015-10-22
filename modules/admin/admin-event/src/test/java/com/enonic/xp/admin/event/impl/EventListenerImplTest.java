package com.enonic.xp.admin.event.impl;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationUpdatedEvent;

import static com.enonic.xp.app.ApplicationEventType.INSTALLED;
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
        final ApplicationUpdatedEvent event = new ApplicationUpdatedEvent( ApplicationKey.from( "module" ), INSTALLED );
        eventListener.onEvent( event );

        verify( this.webSocketManager, atLeastOnce() ).sendToAll( anyString() );
    }

    @Test
    public void testUnsupportedEvent()
        throws Exception
    {
        final TestEvent event = new TestEvent();
        eventListener.onEvent( event );

        verify( this.webSocketManager, never() ).sendToAll( anyString() );
    }
}
