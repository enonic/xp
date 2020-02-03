package com.enonic.xp.web.jetty.impl;

import java.util.Dictionary;
import java.util.List;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Handler;
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

import com.codahale.metrics.jetty9.InstrumentedHandler;

import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.util.Metrics;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.jetty.impl.configurator.GZipConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.HttpConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.MultipartConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.RequestLogConfigurator;
import com.enonic.xp.web.jetty.impl.configurator.SessionConfigurator;
import com.enonic.xp.web.jetty.impl.session.JettySessionStorageConfigurator;

@Component(immediate = true, configurationPid = "com.enonic.xp.web.jetty")
public final class JettyActivator
{
    private final static Logger LOG = LoggerFactory.getLogger( JettyActivator.class );

    private final BundleContext bundleContext;

    private Server server;

    private ServiceRegistration<Server> serverServiceRegistration;

    private ServletContext xpServletContext;

    private ServiceRegistration<ServletContext> xpServletContextReg;

    private final JettySessionStorageConfigurator jettySessionStorageConfigurator;

    private final ContextHandlerCollection contexts = new ContextHandlerCollection();

    private final JettyConfig config;

    @Activate
    public JettyActivator( final JettyConfig config, final BundleContext bundleContext,
                           @Reference final JettySessionStorageConfigurator jettySessionStorageConfigurator,
                           @Reference final List<DispatchServlet> dispatchServlets )
    {
        this.config = config;
        this.bundleContext = bundleContext;
        this.jettySessionStorageConfigurator = jettySessionStorageConfigurator;
        dispatchServlets.stream().map( this::initServletContextHandler ).forEach( contexts::addHandler );
    }

    @Activate
    public void activate()
        throws Exception
    {
        fixJettyVersion();

        start();
        publishXpServletContext();
    }


    @Deactivate
    public void deactivate()
        throws Exception
    {
        unpublishXpServletContext();
        stop();
    }

    private void unpublishXpServletContext()
    {
        if ( xpServletContextReg != null )
        {
            xpServletContextReg.unregister();
        }
    }

    private void publishXpServletContext()
    {
        if ( xpServletContext != null )
        {
            xpServletContextReg = bundleContext.registerService( ServletContext.class, xpServletContext,
                                                                 Dictionaries.of( DispatchConstants.CONNECTOR_PROPERTY,
                                                                                  DispatchConstants.XP_CONNECTOR ) );
        }
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
        final Server server = createServer();

        jettySessionStorageConfigurator.configure( server );
        new HttpConfigurator().configure( this.config, server );
        new RequestLogConfigurator().configure( this.config, server );

        Metrics.removeAll( Handler.class );
        final InstrumentedHandler instrumentedHandler = new InstrumentedHandler( Metrics.registry(), Handler.class.getName() );
        instrumentedHandler.setHandler( contexts );

        server.setHandler( instrumentedHandler );

        server.start();
        this.server = server;

        this.serverServiceRegistration = bundleContext.registerService( Server.class, this.server, null );

        LOG.info( "Started Jetty" );
        LOG.info( "Listening on ports [{}](xp), [{}](management) and [{}](monitoring)", config.http_xp_port(),
                  config.http_management_port(), config.http_monitor_port() );
    }

    private Server createServer()
    {
        final int maxThreads = config.threadPool_maxThreads();
        final int minThreads = config.threadPool_minThreads();
        final int idleTimeout = config.threadPool_idleTimeout();

        final QueuedThreadPool threadPool = new QueuedThreadPool( maxThreads, minThreads, idleTimeout );
        return new Server( threadPool );
    }

    private void stop()
        throws Exception
    {
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
