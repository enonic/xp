package com.enonic.xp.portal.impl.websocket;

import org.junit.jupiter.api.Test;

import jakarta.websocket.Session;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketEndpointImplTest
{
    @Test
    void onEvent_pinsPerSession()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final ControllerScript pinned = mock( ControllerScript.class );
        when( script.pinned( "session-1" ) ).thenReturn( pinned );

        final Session session = mock( Session.class );
        when( session.getId() ).thenReturn( "session-1" );

        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, () -> script );

        final WebSocketEvent event = WebSocketEvent.create().type( WebSocketEventType.MESSAGE ).session( session ).build();
        endpoint.onEvent( event );

        verify( pinned ).onSocketEvent( event );
    }

    @Test
    void onEvent_withoutSession_isUnpinned()
    {
        final ControllerScript script = mock( ControllerScript.class );

        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, () -> script );

        final WebSocketEvent event = WebSocketEvent.create().type( WebSocketEventType.ERROR ).build();
        endpoint.onEvent( event );

        verify( script ).onSocketEvent( event );
    }
}
