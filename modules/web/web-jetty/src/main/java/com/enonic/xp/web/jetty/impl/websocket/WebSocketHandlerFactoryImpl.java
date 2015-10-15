package com.enonic.xp.web.jetty.impl.websocket;

import javax.servlet.ServletException;

import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.jetty.impl.JettyController;
import com.enonic.xp.web.websocket.WebSocketHandler;
import com.enonic.xp.web.websocket.WebSocketHandlerFactory;

@Component(immediate = true)
public final class WebSocketHandlerFactoryImpl
    implements WebSocketHandlerFactory
{
    private JettyController controller;

    @Override
    public WebSocketHandler create()
        throws ServletException
    {
        final WebSocketPolicy policy = WebSocketPolicy.newServerPolicy();
        final WebSocketServerFactory serverFactory = new WebSocketServerFactory( policy );

        final WebSocketHandlerImpl handler = new WebSocketHandlerImpl( serverFactory );
        handler.init( this.controller.getServletContext() );
        return handler;
    }

    @Reference
    public void setController( final JettyController controller )
    {
        this.controller = controller;
    }
}
