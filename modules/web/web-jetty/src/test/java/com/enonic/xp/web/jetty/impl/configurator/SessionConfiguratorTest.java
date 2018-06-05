package com.enonic.xp.web.jetty.impl.configurator;


import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class SessionConfiguratorTest
    extends JettyConfiguratorTest<SessionHandler>
{
    @Override
    protected SessionHandler setupObject()
    {
        final ServletContextHandler context = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        return context.getSessionHandler();
    }

    @Override
    protected JettyConfigurator<SessionHandler> newConfigurator()
    {
        return new SessionConfigurator();
    }

    @Test
    public void defaultConfig()
    {
        configure();

        assertEquals( 3600, this.object.getMaxInactiveInterval() );
        assertEquals( "JSESSIONID", this.object.getSessionCookieConfig().getName() );
    }

    @Test
    public void overrideConfig()
    {
        Mockito.when( this.config.session_timeout() ).thenReturn( 1 );
        Mockito.when( this.config.session_cookieName() ).thenReturn( "mycookie" );

        configure();

        assertEquals( 60, this.object.getMaxInactiveInterval() );
        assertEquals( "mycookie", this.object.getSessionCookieConfig().getName() );
    }
}