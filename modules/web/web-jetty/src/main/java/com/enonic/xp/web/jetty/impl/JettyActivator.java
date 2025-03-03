package com.enonic.xp.web.jetty.impl;

import java.util.List;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.SessionHandler;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.server.RunMode;
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

    private final JettySessionStoreConfigurator jettySessionStoreConfigurator;

    private final List<DispatchServlet> dispatchServlets;

    private final JettyConfig config;

    private volatile Server server;

    private volatile ServiceRegistration<Server> serverServiceRegistration;

    @Activate
    public JettyActivator( final JettyConfig config, final BundleContext bundleContext,
                           @Reference final JettySessionStoreConfigurator jettySessionStoreConfigurator,
                           @Reference final List<DispatchServlet> dispatchServlets )
    {
        this.config = config;
        this.bundleContext = bundleContext;
        this.jettySessionStoreConfigurator = jettySessionStoreConfigurator;
        this.dispatchServlets = dispatchServlets;
    }

    @Activate
    public void activate()
        throws Exception
    {
        //OldMetrics.removeAll( InstrumentedQueuedThreadPool.class );
        //final QueuedThreadPool threadPool = new InstrumentedQueuedThreadPool( OldMetrics.registry(), maxThreads, minThreads, idleTimeout );
        final QueuedThreadPool threadPool =
            new QueuedThreadPool( config.threadPool_maxThreads(), config.threadPool_minThreads(), config.threadPool_idleTimeout() );
        final Server server = new Server( threadPool );

        this.jettySessionStoreConfigurator.configure( server );
        new HttpConfigurator().configure( this.config, server );
        new RequestLogConfigurator().configure( this.config, server );
        new ErrorHandlerConfigurator().configure( RunMode.get(), server );

        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        ServletContextHandler xpServletContextHandler = null;
        for ( DispatchServlet dispatchServlet : this.dispatchServlets )
        {
            ServletContextHandler servletContextHandler = initServletContextHandler( dispatchServlet );
            if ( DispatchConstants.XP_CONNECTOR.equals( dispatchServlet.getConnector() ) )
            {
                xpServletContextHandler = servletContextHandler;
            }
            contexts.addHandler( servletContextHandler );
        }

        server.setHandler( contexts );

        if ( xpServletContextHandler != null )
        {
            JakartaWebSocketServletContainerInitializer.configure( xpServletContextHandler,
                                                                   ( context, configurator ) -> configurator.setDefaultMaxSessionIdleTimeout(
                                                                       config.websocket_idleTimeout() ) );
        }

        this.server = server;

        this.server.start();

        this.serverServiceRegistration = bundleContext.registerService( Server.class, this.server, null );

        LOG.info( "Started Jetty" );
        LOG.info( "Listening on ports [{}](xp), [{}](management) and [{}](monitoring)", config.http_xp_port(),
                  config.http_management_port(), config.http_monitor_port() );
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        this.serverServiceRegistration.unregister();
        this.server.stop();
        this.server.destroy();
        LOG.info( "Stopped Jetty" );
    }

    private ServletContextHandler initServletContextHandler( final DispatchServlet servlet )
    {
        final ServletContextHandler context = new ServletContextHandler( "/", ServletContextHandler.SESSIONS );
        final SessionHandler sessionHandler = context.getSessionHandler();

        final ServletHolder holder = new ServletHolder( servlet );
        holder.setAsyncSupported( true );
        context.addServlet( holder, "/*" );
        context.setVirtualHosts( List.of( DispatchConstants.VIRTUAL_HOST_PREFIX + servlet.getConnector() ) );

        new SessionConfigurator().configure( config, sessionHandler );
        new GZipConfigurator().configure( config, context );
        new MultipartConfigurator().configure( config, holder );

        return context;
    }
}
