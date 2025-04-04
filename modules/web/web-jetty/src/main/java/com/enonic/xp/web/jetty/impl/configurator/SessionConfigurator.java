package com.enonic.xp.web.jetty.impl.configurator;

import java.util.Arrays;
import java.util.Collections;

import org.eclipse.jetty.ee10.servlet.SessionHandler;
import org.eclipse.jetty.http.HttpCookie;

import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;

import static com.google.common.base.Strings.nullToEmpty;

public final class SessionConfigurator
    extends JettyConfigurator<SessionHandler>
{
    @Override
    protected void doConfigure()
    {
        this.object.setSessionTrackingModes( Collections.singleton( SessionTrackingMode.COOKIE ) );
        this.object.setCheckingRemoteSessionIdEncoding( true );
        this.object.setHttpOnly( true );

        this.object.setSessionCookie( this.config.session_cookieName() );
        this.object.setMaxInactiveInterval( this.config.session_timeout() * 60 );
        setSameSite( this.config.session_cookieSameSite() );

        final SessionCookieConfig cookie = this.object.getSessionCookieConfig();
        cookie.setSecure( this.config.session_cookieAlwaysSecure() );
    }

    private void setSameSite( final String value )
    {
        if ( nullToEmpty( value ).isBlank() )
        {
            return;
        }
        final HttpCookie.SameSite sameSite =
            Arrays.stream( HttpCookie.SameSite.values() ).filter( v -> v.getAttributeValue().equals( value ) ).findAny().orElseThrow();
        this.object.setSameSite( sameSite );
    }
}
