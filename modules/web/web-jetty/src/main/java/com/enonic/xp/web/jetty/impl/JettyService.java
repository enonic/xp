package com.enonic.xp.web.jetty.impl;

// import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import javax.servlet.Servlet;

import org.apache.felix.http.base.internal.EventDispatcher;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.jetty.impl.configurator.HttpConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.MultipartConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.SessionConfigurator;

// http://www.eclipse.org/jetty/documentation/current/configuring-connectors.html
// https://github.com/apache/felix/blob/trunk/http/jetty/src/main/java/org/apache/felix/http/jetty/internal/JettyService.java
final class JettyService
{
    private final static Logger LOG = LoggerFactory.getLogger( JettyService.class );

    protected Server server;

    protected JettyConfig config;

    protected EventDispatcher eventDispatcher;

    protected Servlet dispatcherServlet;

    public void start()
    {
        try
        {
            startJetty();
        }
        catch ( final Exception e )
        {
            stop();
            LOG.error( "Exception while starting Jetty", e );
        }
    }

    public void stop()
    {
        if ( this.server == null )
        {
            return;
        }

        try
        {
            stopJetty();
        }
        catch ( final Exception e )
        {
            LOG.error( "Exception while stopping Jetty", e );
        }
    }

    private void startJetty()
        throws Exception
    {
        this.server = new Server();

        final ServletContextHandler context = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        new SessionConfigurator().configure( this.config, context.getSessionHandler().getSessionManager() );

        final ServletHolder holder = new ServletHolder( this.dispatcherServlet );
        holder.setAsyncSupported( true );
        context.addServlet( holder, "/*" );

        new MultipartConfigurator().configure( this.config, holder );
        new HttpConfigurator().configure( this.config, this.server );

        this.server.setHandler( context );
        this.eventDispatcher.setActive( true );
        this.server.start();
    }

    private void stopJetty()
        throws Exception
    {
        this.eventDispatcher.setActive( false );

        this.server.stop();
        this.server = null;
        LOG.info( "Stopped Jetty" );
    }
}
