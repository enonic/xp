package com.enonic.xp.portal.impl.sse;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.sse.SseConfig;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SseEndpointImplTest
{
    @Test
    void getConfig()
    {
        final SseConfig config = SseConfig.empty();
        final SseEndpointImpl endpoint = new SseEndpointImpl( config, () -> mock( ControllerScript.class ) );
        assertSame( config, endpoint.getConfig() );
    }

    @Test
    void onEvent_delegatesToScript()
    {
        final ControllerScript script = mock( ControllerScript.class );
        final SseEndpointImpl endpoint = new SseEndpointImpl( SseConfig.empty(), () -> script );

        final SseEvent event = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( UUID.randomUUID() )
            .attributes( GenericValue.newObject().build() )
            .build();
        endpoint.onEvent( event );

        verify( script ).onSseEvent( event );
    }
}
