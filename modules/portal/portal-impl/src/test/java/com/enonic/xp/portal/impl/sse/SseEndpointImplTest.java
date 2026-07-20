package com.enonic.xp.portal.impl.sse;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.sse.SseConfig;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SseEndpointImplTest
{
    @Test
    void getConfig()
    {
        final SseConfig config = SseConfig.empty();
        final SseEndpointImpl endpoint = new SseEndpointImpl( config, mock( ControllerScript.class ) );
        assertSame( config, endpoint.getConfig() );
    }

    @Test
    void open_retainsTheBoundContext()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final SseEndpointImpl endpoint = new SseEndpointImpl( SseConfig.empty(), script );

        final SseEvent open = event( SseEventType.OPEN );
        endpoint.onEvent( open );

        verify( script ).retain();
        verify( script ).onSseEvent( open );
        verify( script, never() ).release();
    }

    @Test
    void timeoutThenClose_releasesExactlyOnce()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final SseEndpointImpl endpoint = new SseEndpointImpl( SseConfig.empty(), script );

        endpoint.onEvent( event( SseEventType.OPEN ) );
        endpoint.onEvent( event( SseEventType.TIMEOUT ) );
        endpoint.onEvent( event( SseEventType.CLOSE ) );

        verify( script, times( 1 ) ).retain();
        verify( script, times( 1 ) ).release();
    }

    @Test
    void failedOpen_releasesThePin()
    {
        final ControllerScript script = mock( ControllerScript.class );
        doThrow( new RuntimeException( "open handler failed" ) ).when( script ).onSseEvent( any() );
        final SseEndpointImpl endpoint = new SseEndpointImpl( SseConfig.empty(), script );

        // a failed open gets no terminal event: the pin must not leak the slot out of the pool
        assertThrows( RuntimeException.class, () -> endpoint.onEvent( event( SseEventType.OPEN ) ) );
        verify( script ).retain();
        verify( script, times( 1 ) ).release();

        // a late CLOSE (the manager completing the failed connection) must not double-release
        assertThrows( RuntimeException.class, () -> endpoint.onEvent( event( SseEventType.CLOSE ) ) );
        verify( script, times( 1 ) ).release();
    }

    private static SseEvent event( final SseEventType type )
    {
        return SseEvent.create().type( type ).clientId( UUID.randomUUID() ).attributes( GenericValue.newObject().build() ).build();
    }
}
