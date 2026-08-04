package com.enonic.xp.portal.impl.websocket;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class WebSocketApiEndpointImplTest
{
    @Test
    void getConfig()
    {
        final WebSocketConfig config = new WebSocketConfig();
        final WebSocketApiEndpointImpl endpoint =
            new WebSocketApiEndpointImpl( config, () -> mock( UniversalApiHandler.class ), null );
        assertSame( config, endpoint.getConfig() );
    }

    @Test
    void exposesItsApplication()
    {
        final ApplicationKey app = ApplicationKey.from( "myapp" );
        final WebSocketApiEndpointImpl endpoint =
            new WebSocketApiEndpointImpl( new WebSocketConfig(), () -> mock( UniversalApiHandler.class ), app );
        assertSame( app, endpoint.getApplication() );
    }

    @Test
    void onEvent_delegatesToApiHandler()
    {
        final UniversalApiHandler handler = mock( UniversalApiHandler.class );
        final WebSocketApiEndpointImpl endpoint = new WebSocketApiEndpointImpl( new WebSocketConfig(), () -> handler, null );

        final WebSocketEvent event = WebSocketEvent.create().type( WebSocketEventType.OPEN ).build();
        endpoint.onEvent( event );

        verify( handler ).onSocketEvent( event );
    }
}
