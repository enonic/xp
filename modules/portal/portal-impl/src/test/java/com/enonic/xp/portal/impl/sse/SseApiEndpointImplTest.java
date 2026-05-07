package com.enonic.xp.portal.impl.sse;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.sse.SseConfig;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SseApiEndpointImplTest
{
    @Test
    void getConfig()
    {
        final SseConfig config = SseConfig.empty();
        final SseApiEndpointImpl endpoint = new SseApiEndpointImpl( config, () -> mock( UniversalApiHandler.class ) );
        assertSame( config, endpoint.getConfig() );
    }

    @Test
    void onEvent_delegatesToApiHandler()
    {
        final UniversalApiHandler handler = mock( UniversalApiHandler.class );
        final SseApiEndpointImpl endpoint = new SseApiEndpointImpl( SseConfig.empty(), () -> handler );

        final SseEvent event = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( UUID.randomUUID() )
            .attributes( GenericValue.newObject().build() )
            .build();
        endpoint.onEvent( event );

        verify( handler ).onSseEvent( event );
    }
}
