package com.enonic.xp.portal.impl.websocket;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.websocket.WebSocketContext;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketService;

final class WebSocketContextImpl
    implements WebSocketContext
{
    WebSocketService webSocketService;

    HttpServletRequest request;

    HttpServletResponse response;

    WebSocketRegistry registry;

    @Override
    public boolean apply( final WebSocketEndpoint endpoint )
        throws IOException
    {
        final EndpointFactoryImpl factory = new EndpointFactoryImpl();
        factory.registry = this.registry;
        factory.endpoint = endpoint;
        return this.webSocketService.acceptWebSocket( this.request, this.response, factory );
    }
}
