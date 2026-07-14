package com.enonic.xp.portal.impl.websocket;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebSocketEndpointImplTest
{
    @Test
    void open_retainsTheBoundContext()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, script );

        final WebSocketEvent open = WebSocketEvent.create().type( WebSocketEventType.OPEN ).build();
        endpoint.onEvent( open );

        verify( script ).retain();
        verify( script ).onSocketEvent( open );
        verify( script, never() ).release();
    }

    @Test
    void message_dispatchesWithoutLifecycleChanges()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, script );

        final WebSocketEvent message = WebSocketEvent.create().type( WebSocketEventType.MESSAGE ).build();
        endpoint.onEvent( message );

        verify( script ).onSocketEvent( message );
        verify( script, never() ).retain();
        verify( script, never() ).release();
    }

    @Test
    void errorThenClose_releasesExactlyOnce()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, script );

        endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.OPEN ).build() );
        endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.ERROR ).build() );
        endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.CLOSE ).build() );

        verify( script, times( 1 ) ).retain();
        verify( script, times( 1 ) ).release();
    }
}
