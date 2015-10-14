package com.enonic.xp.web.jetty.impl.websocket;

import javax.servlet.ServletContext;

import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;

import com.enonic.xp.web.websocket.WebSocketHandler;
import com.enonic.xp.web.websocket.WebSocketHandlerFactory;

final class WebSocketHandlerFactoryImpl
    implements WebSocketHandlerFactory
{
    @Override
    public WebSocketHandler create()
    {
        final WebSocketPolicy policy = WebSocketPolicy.newServerPolicy();
        final WebSocketServerFactory serverFactory = new WebSocketServerFactory( policy );
        return new WebSocketHandlerImpl( serverFactory );
    }

    public void configure( final ServletContext context )
    {
        context.setAttribute( WebSocketHandlerFactory.class.getName(), this );
    }
}
