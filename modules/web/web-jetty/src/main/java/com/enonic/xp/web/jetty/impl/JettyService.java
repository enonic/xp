package com.enonic.xp.web.jetty.impl;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionCache;
import org.eclipse.jetty.server.session.SessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.jetty9.InstrumentedHandler;

import com.enonic.xp.util.Metrics;
import com.enonic.xp.web.jetty.impl.configurator.GZipConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.HttpConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.MultipartConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.RequestLogConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.SessionConfigurator;

final class JettyService
{
    private final static Logger LOG = LoggerFactory.getLogger( JettyService.class );

    protected Server server;

    protected JettyConfig config;

    protected Servlet dispatcherServlet;

    protected ServletContextHandler context;

    protected String workerName;

    protected SessionDataStore sessionDataStore;

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

        this.context = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        final SessionHandler sessionHandler = this.context.getSessionHandler();
        new SessionConfigurator().configure( this.config, sessionHandler );
        new GZipConfigurator().configure( this.config, this.context );
        new RequestLogConfigurator().configure( this.config, this.server );

        final ServletHolder holder = new ServletHolder( this.dispatcherServlet );
        holder.setAsyncSupported( true );
        this.context.addServlet( holder, "/*" );

        new MultipartConfigurator().configure( this.config, holder );
        new HttpConfigurator().configure( this.config, this.server );

        Metrics.removeAll( Handler.class );
        final InstrumentedHandler instrumentedHandler = new InstrumentedHandler( Metrics.registry(), Handler.class.getName() );
        instrumentedHandler.setHandler( this.context );

        this.server.setHandler( instrumentedHandler );

        final DefaultSessionIdManager sessionManager = new DefaultSessionIdManager( this.server );
        sessionManager.setWorkerName( this.workerName );
        this.server.setSessionIdManager( sessionManager );

        if ( sessionDataStore != null )
        {
            final NullSessionCache sessionCache = new NullSessionCache( sessionHandler );
            sessionCache.setSaveOnCreate( true );
            sessionCache.setSaveOnInactiveEviction( true );
            sessionCache.setRemoveUnloadableSessions( true );
            sessionCache.setSessionDataStore( sessionDataStore );
            sessionHandler.setSessionCache( sessionCache );
        }

        this.server.start();
    }

    private void stopJetty()
        throws Exception
    {
        this.server.stop();
        this.server = null;
        LOG.info( "Stopped Jetty" );
    }
}
