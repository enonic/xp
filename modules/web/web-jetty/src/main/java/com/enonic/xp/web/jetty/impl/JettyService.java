package com.enonic.xp.web.jetty.impl;

import javax.servlet.Servlet;
import java.lang.management.ManagementFactory;

import com.codahale.metrics.jetty9.InstrumentedHandler;
import com.enonic.xp.util.Metrics;
import com.enonic.xp.web.jetty.impl.configurator.*;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class JettyService
{
    private final static Logger LOG = LoggerFactory.getLogger( JettyService.class );

    protected Server server;

    protected JettyConfig config;

    protected Servlet dispatcherServlet;

    protected ServletContextHandler context;

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
        new SessionConfigurator().configure( this.config, this.context.getSessionHandler().getSessionManager() );
        new GZipConfigurator().configure( this.config, this.context );
        new RequestLogConfigurator().configure( this.config, this.server );

        final ServletHolder holder = new ServletHolder( this.dispatcherServlet );
        holder.setAsyncSupported( true );
        this.context.addServlet( holder, "/*" );

        new MultipartConfigurator().configure( this.config, holder );
        new HttpConfigurator().configure( this.config, this.server );

        // Setup JMX
        if ( this.config.jmx_enabled() ) {
            MBeanContainer mbContainer = new MBeanContainer( ManagementFactory.getPlatformMBeanServer() );
            server.addEventListener( mbContainer );
            server.addBean( mbContainer );
        }

        Metrics.removeAll( Handler.class );
        final InstrumentedHandler instrumentedHandler = new InstrumentedHandler( Metrics.registry(), Handler.class.getName() );
        instrumentedHandler.setHandler( this.context );

        this.server.setHandler( instrumentedHandler );
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
