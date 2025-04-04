package com.enonic.xp.web.jetty.impl.configurator;


import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.SessionHandler;
import org.eclipse.jetty.http.HttpCookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionConfiguratorTest
    extends JettyConfiguratorTest<SessionHandler>
{
    @Override
    protected SessionHandler setupObject()
    {
        final ServletContextHandler context = new ServletContextHandler(  "/", ServletContextHandler.SESSIONS );
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
        assertEquals( HttpCookie.SameSite.LAX, this.object.getSameSite() );
        assertFalse( this.object.isSecureCookies() );
        assertTrue( this.object.isHttpOnly() );
    }

    @Test
    public void overrideConfig()
    {
        Mockito.when( this.config.session_timeout() ).thenReturn( 1 );
        Mockito.when( this.config.session_cookieName() ).thenReturn( "mycookie" );
        Mockito.when( this.config.session_cookieSameSite() ).thenReturn( "Strict" );
        Mockito.when( this.config.session_cookieAlwaysSecure() ).thenReturn( true );

        configure();

        assertEquals( 60, this.object.getMaxInactiveInterval() );
        assertEquals( "mycookie", this.object.getSessionCookieConfig().getName() );
        assertEquals( HttpCookie.SameSite.STRICT, this.object.getSameSite() );
        assertTrue( this.object.isSecureCookies() );
    }

    @Test
    public void sameSiteNotSet()
    {
        Mockito.when( this.config.session_cookieSameSite() ).thenReturn( "" );

        configure();
        assertNull( this.object.getSameSite() );
    }
}
