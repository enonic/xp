package com.enonic.xp.web.impl;

import java.io.File;
import java.util.Collections;

import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.enonic.xp.web.server.WebServerConfig;

final class JettyConfigurator
{
    private final WebServerConfig config;

    JettyConfigurator( final WebServerConfig config )
    {
        this.config = config;
    }

    void configure( final ServerConnector connector )
    {
        connector.setHost( this.config.getHost() );
        connector.setIdleTimeout( this.config.getIdleTimeout() );
        connector.getConnectionFactories().forEach( this::configure );
    }

    void configure( final Server server )
    {
        if ( !this.config.isLogEnabled() )
        {
            return;
        }

        final NCSARequestLog log = new NCSARequestLog();
        log.setAppend( this.config.isLogAppend() );
        log.setExtended( this.config.isLogExtended() );
        log.setLogTimeZone( this.config.getLogTimeZone() );
        log.setRetainDays( this.config.getLogRetainDays() );

        final File logFile = this.config.getLogFile();
        if ( logFile == null )
        {
            return;
        }

        log.setFilename( logFile.getAbsolutePath() );
        server.setRequestLog( log );
    }

    private void configure( final ConnectionFactory factory )
    {
        if ( factory instanceof HttpConnectionFactory )
        {
            configure( (HttpConnectionFactory) factory );
        }
    }

    private void configure( final HttpConnectionFactory factory )
    {
        // Add customizers.
        factory.getHttpConfiguration().addCustomizer( new ForwardedRequestCustomizer() );

        // Configure the factory.
        final HttpConfiguration config = factory.getHttpConfiguration();
        config.setSendDateHeader( true );
        config.setSendServerVersion( this.config.getSendServerHeader() );
        config.setSendXPoweredBy( this.config.getSendServerHeader() );
        config.setRequestHeaderSize( this.config.getRequestHeaderSize() );
        config.setResponseHeaderSize( this.config.getResponseHeaderSize() );
    }

    void configure( final ServletContextHandler contextHandler )
    {
        if ( !this.config.isGzipEnabled() )
        {
            return;
        }

        final GzipHandler handler = new GzipHandler();
        handler.setMinGzipSize( this.config.getGzipMinSize() );
        handler.addExcludedMimeTypes( "application/octet-stream" );

        contextHandler.setGzipHandler( handler );

        configure( contextHandler.getSessionHandler() );
    }

    private void configure( final SessionHandler sessionHandler )
    {
        sessionHandler.setMaxInactiveInterval( this.config.getSessionTimeout() * 60 );
        sessionHandler.setSessionTrackingModes( Collections.singleton( SessionTrackingMode.COOKIE ) );
        sessionHandler.setCheckingRemoteSessionIdEncoding( true );
        configure( sessionHandler.getSessionCookieConfig() );
    }

    private void configure( final SessionCookieConfig cookie )
    {
        cookie.setName( this.config.getSessionCookieName() );
        cookie.setDomain( null );
        cookie.setSecure( false );
        cookie.setHttpOnly( true );
        cookie.setPath( null );
        cookie.setMaxAge( -1 );
    }
}
