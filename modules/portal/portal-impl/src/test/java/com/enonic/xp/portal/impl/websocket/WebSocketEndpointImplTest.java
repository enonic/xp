package com.enonic.xp.portal.impl.websocket;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WebSocketEndpointImplTest
{
    @Test
    void exposesItsApplication()
    {
        final ApplicationKey app = ApplicationKey.from( "myapp" );
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, mock( ControllerScript.class ), app );
        assertSame( app, endpoint.getApplication() );
    }

    @Test
    void open_retainsTheBoundContext()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, script, null );

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
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, script, null );

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
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, script, null );

        endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.OPEN ).build() );
        endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.ERROR ).build() );
        endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.CLOSE ).build() );

        verify( script, times( 1 ) ).retain();
        verify( script, times( 1 ) ).release();
    }

    @Test
    void failedOpen_releasesThePin()
    {
        final ControllerScript script = mock( ControllerScript.class );
        doThrow( new RuntimeException( "open handler failed" ) ).when( script ).onSocketEvent( any() );
        final WebSocketEndpointImpl endpoint = new WebSocketEndpointImpl( null, script, null );

        // release without waiting for the container's ERROR/CLOSE — pool capacity must not
        // depend on container semantics
        assertThrows( RuntimeException.class,
                      () -> endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.OPEN ).build() ) );
        verify( script ).retain();
        verify( script, times( 1 ) ).release();

        // the terminal events the container then fires must not double-release
        assertThrows( RuntimeException.class,
                      () -> endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.ERROR ).build() ) );
        assertThrows( RuntimeException.class,
                      () -> endpoint.onEvent( WebSocketEvent.create().type( WebSocketEventType.CLOSE ).build() ) );
        verify( script, times( 1 ) ).release();
    }
}
