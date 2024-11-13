package com.enonic.xp.portal.impl.websocket;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.websocket.EndpointFactory;
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
        if ( endpoint instanceof WebSocketApiEndpointImpl )
        {
            EndpointFactory factory = ( (WebSocketApiEndpointImpl) endpoint ).getEndpointFactory();
            return this.webSocketService.acceptWebSocket( this.request, this.response, factory );
        }
        else
        {
            final EndpointFactoryImpl factory = new EndpointFactoryImpl();
            factory.registry = this.registry;
            factory.endpoint = endpoint;
            return this.webSocketService.acceptWebSocket( this.request, this.response, factory );
        }
    }
}
