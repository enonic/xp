package com.enonic.xp.web.jetty.impl.websocket;

import javax.servlet.ServletContext;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Test;

import com.enonic.xp.web.jetty.impl.configurator.JettyConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.JettyConfiguratorTest;
import com.enonic.xp.web.websocket.WebSocketHandlerFactory;

import static org.junit.Assert.*;

public class WebSocketConfiguratorTest
    extends JettyConfiguratorTest<ServletContextHandler>
{
    @Override
    protected ServletContextHandler setupObject()
    {
        return new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
    }

    @Override
    protected JettyConfigurator<ServletContextHandler> newConfigurator()
    {
        return new WebSocketConfigurator();
    }

    @Test
    public void testConfigure()
    {
        final ServletContext context = this.object.getServletContext();
        assertNull( context.getAttribute( WebSocketHandlerFactory.class.getName() ) );

        configure();

        assertNotNull( context.getAttribute( WebSocketHandlerFactory.class.getName() ) );
    }
}
