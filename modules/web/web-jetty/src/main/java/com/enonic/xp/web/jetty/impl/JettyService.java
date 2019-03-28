package com.enonic.xp.web.jetty.impl;

import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
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
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.jetty.impl.configurator.GZipConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.HttpConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.MultipartConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.SessionConfigurator;

final class JettyService
{
    private final static Logger LOG = LoggerFactory.getLogger( JettyService.class );

    protected Server server;

    protected JettyConfig config;

    protected List<DispatchServlet> dispatcherServlets;

    protected ContextHandlerCollection contexts;

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

        this.contexts = new ContextHandlerCollection();

        this.dispatcherServlets.stream().
            map( this::initServletContextHandler ).
            forEach( contexts::addHandler );

        new HttpConfigurator().configure( this.config, this.server );

        Metrics.removeAll( Handler.class );
        final InstrumentedHandler instrumentedHandler = new InstrumentedHandler( Metrics.registry(), Handler.class.getName() );
        instrumentedHandler.setHandler( contexts );

        this.server.setHandler( contexts );

        final DefaultSessionIdManager sessionManager = new DefaultSessionIdManager( this.server );
        sessionManager.setWorkerName( this.workerName );
        this.server.setSessionIdManager( sessionManager );

        this.server.start();
        LOG.info( "Started Jetty" );
        LOG.info( "Listening on ports [{}](xp), [{}](management) and [{}](monitoring)", config.http_xp_port(),
                  config.http_management_port(), config.http_monitor_port() );
    }

    private ServletContextHandler initServletContextHandler( final DispatchServlet servlet )
    {
        final ServletContextHandler context = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        final SessionHandler sessionHandler = context.getSessionHandler();

        final ServletHolder holder = new ServletHolder( servlet );
        holder.setAsyncSupported( true );
        context.addServlet( holder, "/*" );
        context.setVirtualHosts( new String[]{DispatchConstants.VIRTUAL_HOST_PREFIX + servlet.getConnector()} );

        new SessionConfigurator().configure( this.config, sessionHandler );
        new GZipConfigurator().configure( this.config, context );
        new MultipartConfigurator().configure( this.config, holder );

        if ( sessionDataStore != null )
        {
            final NullSessionCache sessionCache = new NullSessionCache( sessionHandler );
            sessionCache.setSaveOnCreate( true );
            sessionCache.setSaveOnInactiveEviction( true );
            sessionCache.setRemoveUnloadableSessions( true );
            sessionCache.setSessionDataStore( sessionDataStore );
            sessionHandler.setSessionCache( sessionCache );
        }

        return context;
    }

    private void stopJetty()
        throws Exception
    {
        this.server.stop();
        this.server = null;
        LOG.info( "Stopped Jetty" );
    }
}
