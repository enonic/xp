package com.enonic.xp.web.jetty.impl.configurator;

import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class SessionConfiguratorTest
    extends JettyConfiguratorTest<SessionManager>
{
    @Override
    protected SessionManager setupObject()
    {
        final ServletContextHandler context = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        return context.getSessionHandler().getSessionManager();
    }

    @Override
    protected JettyConfigurator<SessionManager> newConfigurator()
    {
        return new SessionConfigurator();
    }

    @Test
    public void defaultConfig()
    {
        configure();

        assertEquals( 3600000, this.object.getMaxInactiveInterval() );
        assertEquals( "JSESSIONID", this.object.getSessionCookieConfig().getName() );
    }

    @Test
    public void overrideConfig()
    {
        Mockito.when( this.config.session_timeout() ).thenReturn( 1 );
        Mockito.when( this.config.session_cookieName() ).thenReturn( "mycookie" );

        configure();

        assertEquals( 60000, this.object.getMaxInactiveInterval() );
        assertEquals( "mycookie", this.object.getSessionCookieConfig().getName() );
    }
}