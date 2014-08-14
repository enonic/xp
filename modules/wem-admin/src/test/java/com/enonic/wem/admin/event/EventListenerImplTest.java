package com.enonic.wem.admin.event;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.event.Event;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleUpdatedEvent;

import static com.enonic.wem.api.module.ModuleState.ACTIVE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class EventListenerImplTest
{

    private EventListenerImpl eventListener;

    @Before
    public final void setUp()
        throws Exception
    {
        eventListener = new EventListenerImpl();
        eventListener.webSocketManager = mock( WebSocketManager.class );
    }

    @Test
    public void testEvent()
        throws Exception
    {
        final ModuleUpdatedEvent event = new ModuleUpdatedEvent( ModuleKey.from( "module-1.0.0" ), ACTIVE );
        eventListener.onEvent( event );

        verify( eventListener.webSocketManager, atLeastOnce() ).sendToAll( anyString() );
    }

    @Test
    public void testUnsupportedEvent()
        throws Exception
    {
        final TestEvent event = new TestEvent();
        eventListener.onEvent( event );

        verify( eventListener.webSocketManager, never() ).sendToAll( anyString() );
    }

    class TestEvent
        implements Event
    {
    }

}