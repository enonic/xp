package com.enonic.xp.web.impl.websocket;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketService;

final class WebSocketContextImpl
    implements WebSocketContext
{
    protected WebSocketService webSocketService;

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected WebSocketRegistry registry;

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
