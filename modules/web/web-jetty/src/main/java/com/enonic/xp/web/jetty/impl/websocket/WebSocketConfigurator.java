package com.enonic.xp.web.jetty.impl.websocket;

import org.eclipse.jetty.servlet.ServletContextHandler;

import com.enonic.xp.web.jetty.impl.configurator.JettyConfigurator;

public final class WebSocketConfigurator
    extends JettyConfigurator<ServletContextHandler>
{
    @Override
    protected void doConfigure()
    {
        new WebSocketHandlerFactoryImpl().configure( this.object.getServletContext() );
    }
}
