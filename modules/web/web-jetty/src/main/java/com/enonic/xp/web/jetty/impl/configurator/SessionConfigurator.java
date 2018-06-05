package com.enonic.xp.web.jetty.impl.configurator;

import java.util.Collections;

import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.eclipse.jetty.server.session.SessionHandler;

public final class SessionConfigurator
    extends JettyConfigurator<SessionHandler>
{
    @Override
    protected void doConfigure()
    {
        this.object.setMaxInactiveInterval( getTimeout() );
        this.object.setSessionTrackingModes( Collections.singleton( SessionTrackingMode.COOKIE ) );
        this.object.setCheckingRemoteSessionIdEncoding( true );
        doConfigure( this.object.getSessionCookieConfig() );
    }

    private void doConfigure( final SessionCookieConfig cookie )
    {
        cookie.setName( getCookieName() );
        cookie.setDomain( null );
        cookie.setSecure( false );
        cookie.setHttpOnly( true );
        cookie.setPath( null );
        cookie.setMaxAge( -1 );
    }

    private int getTimeout()
    {
        return this.config.session_timeout() * 60;
    }

    private String getCookieName()
    {
        return this.config.session_cookieName();
    }
}
