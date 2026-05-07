package com.enonic.xp.portal.impl.api;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.websocket.WebSocketEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamicUniversalApiHandlerTest
{
    private final ApiDescriptor descriptor = ApiDescriptor.create()
        .key( DescriptorKey.from( ApplicationKey.from( "app" ), "api" ) )
        .allowedPrincipals( PrincipalKeys.from( RoleKeys.EVERYONE ) )
        .build();

    @Test
    void handle_delegates()
    {
        final UniversalApiHandler inner = mock( UniversalApiHandler.class );
        final WebRequest request = new WebRequest();
        final WebResponse response = WebResponse.create().build();
        when( inner.handle( request ) ).thenReturn( response );

        final DynamicUniversalApiHandler handler = new DynamicUniversalApiHandler( inner, descriptor );

        assertThat( handler.handle( request ) ).isSameAs( response );
        verify( inner ).handle( request );
    }

    @Test
    void onSseEvent_delegates()
    {
        final UniversalApiHandler inner = mock( UniversalApiHandler.class );
        final DynamicUniversalApiHandler handler = new DynamicUniversalApiHandler( inner, descriptor );

        final SseEvent event = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( UUID.randomUUID() )
            .attributes( GenericValue.newObject().build() )
            .build();

        handler.onSseEvent( event );

        verify( inner ).onSseEvent( event );
    }

    @Test
    void onSocketEvent_delegates()
    {
        final UniversalApiHandler inner = mock( UniversalApiHandler.class );
        final DynamicUniversalApiHandler handler = new DynamicUniversalApiHandler( inner, descriptor );

        final WebSocketEvent event = WebSocketEvent.create().build();
        handler.onSocketEvent( event );

        verify( inner ).onSocketEvent( event );
    }

    @Test
    void getApiDescriptor()
    {
        final DynamicUniversalApiHandler handler = new DynamicUniversalApiHandler( mock( UniversalApiHandler.class ), descriptor );
        assertThat( handler.getApiDescriptor() ).isSameAs( descriptor );
    }
}
