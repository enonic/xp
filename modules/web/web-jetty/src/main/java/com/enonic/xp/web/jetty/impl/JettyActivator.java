package com.enonic.xp.web.jetty.impl;

import java.util.Dictionary;
import java.util.List;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.jetty9.InstrumentedQueuedThreadPool;

import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.server.RunMode;
import com.enonic.xp.util.Metrics;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.jetty.impl.configurator.ErrorHandlerConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.GZipConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.HttpConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.MultipartConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.RequestLogConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.SessionConfigurator;
import com.enonic.xp.web.jetty.impl.session.JettySessionStoreConfigurator;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.jetty")
public final class JettyActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( JettyActivator.class );

    private final BundleContext bundleContext;

    private Server server;

    private ServiceRegistration<Server> serverServiceRegistration;

    private ServletContext xpServletContext;

    private ServiceRegistration<ServletContext> xpServletContextReg;

    private final JettySessionStoreConfigurator jettySessionStoreConfigurator;

    private final ContextHandlerCollection contexts = new ContextHandlerCollection();

    private final JettyConfig config;

    @Activate
    public JettyActivator( final JettyConfig config, final BundleContext bundleContext,
                           @Reference final JettySessionStoreConfigurator jettySessionStoreConfigurator,
                           @Reference final List<DispatchServlet> dispatchServlets )
    {
        this.config = config;
        this.bundleContext = bundleContext;
        this.jettySessionStoreConfigurator = jettySessionStoreConfigurator;
        fixJettyVersion();
        dispatchServlets.stream().map( this::initServletContextHandler ).forEach( contexts::addHandler );
    }

    @Activate
    public void activate()
        throws Exception
    {
        createServer();
        start();
    }


    @Deactivate
    public void deactivate()
        throws Exception
    {
        stop();
    }

    private void fixJettyVersion()
    {
        final Dictionary<String, String> headers = this.bundleContext.getBundle().getHeaders();
        final String version = headers.get( "X-Jetty-Version" );

        if ( version != null )
        {
            System.setProperty( "jetty.version", version );
        }
    }

    private void start()
        throws Exception
    {
        this.server.start();

        this.serverServiceRegistration = bundleContext.registerService( Server.class, this.server, null );

        if ( xpServletContext != null )
        {
            xpServletContextReg = bundleContext.registerService( ServletContext.class, xpServletContext,
                                                                 Dictionaries.of( DispatchConstants.CONNECTOR_PROPERTY,
                                                                                  DispatchConstants.XP_CONNECTOR ) );
        }
        LOG.info( "Started Jetty" );
        LOG.info( "Listening on ports [{}](xp), [{}](management) and [{}](monitoring)", config.http_xp_port(),
                  config.http_management_port(), config.http_monitor_port() );
    }

    private void createServer()
    {
        final int maxThreads = config.threadPool_maxThreads();
        final int minThreads = config.threadPool_minThreads();
        final int idleTimeout = config.threadPool_idleTimeout();

        Metrics.removeAll( InstrumentedQueuedThreadPool.class );
        final QueuedThreadPool threadPool = new InstrumentedQueuedThreadPool( Metrics.registry(), maxThreads, minThreads, idleTimeout );
        final Server server = new Server( threadPool );

        jettySessionStoreConfigurator.configure( server );
        new HttpConfigurator().configure( this.config, server );
        new RequestLogConfigurator().configure( this.config, server );
        new ErrorHandlerConfigurator().configure( RunMode.get(), server );

        server.setHandler( contexts );

        this.server = server;
    }

    private void stop()
        throws Exception
    {
        if ( xpServletContextReg != null )
        {
            xpServletContextReg.unregister();
        }
        if ( this.serverServiceRegistration != null )
        {
            this.serverServiceRegistration.unregister();
            this.server.stop();
            this.server.destroy();
            LOG.info( "Stopped Jetty" );
        }
    }

    private ServletContextHandler initServletContextHandler( final DispatchServlet servlet )
    {
        final ServletContextHandler context = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        final SessionHandler sessionHandler = context.getSessionHandler();

        final ServletHolder holder = new ServletHolder( servlet );
        holder.setAsyncSupported( true );
        context.addServlet( holder, "/*" );
        context.setVirtualHosts( new String[]{DispatchConstants.VIRTUAL_HOST_PREFIX + servlet.getConnector()} );

        new SessionConfigurator().configure( config, sessionHandler );
        new GZipConfigurator().configure( config, context );
        new MultipartConfigurator().configure( config, holder );

        if ( servlet.getConnector().equals( DispatchConstants.XP_CONNECTOR ) )
        {
            xpServletContext = context.getServletContext();
        }
        return context;
    }
}
