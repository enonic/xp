package com.enonic.xp.web.jetty.impl.configurator;

import java.util.Collections;

import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.eclipse.jetty.server.SessionManager;

public final class SessionConfigurator
    extends JettyConfigurator<SessionManager>
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
        cookie.setHttpOnly( false );
        cookie.setPath( null );
        cookie.setMaxAge( -1 );
    }

    private int getTimeout()
    {
        // TODO: Is this a bug? Seems to be in ms instead of sec.
        return this.config.session_timeout() * 60 * 1000;
    }

    private String getCookieName()
    {
        return this.config.session_cookieName();
    }
}
