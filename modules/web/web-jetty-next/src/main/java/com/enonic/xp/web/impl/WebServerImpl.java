package com.enonic.xp.web.impl;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.server.WebServer;
import com.enonic.xp.web.server.WebServerConfig;

final class WebServerImpl
    implements WebServer
{
    private final Server server;

    private final ServerConnector connector;

    private final ServletContextHandler context;

    WebServerImpl( final WebServerConfig config )
    {
        this.server = new Server( config.getPort() );
        this.connector = (ServerConnector) this.server.getConnectors()[0];

        final JettyConfigurator configurator = new JettyConfigurator( config );
        configurator.configure( this.server );
        configurator.configure( this.connector );

        this.context = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        configurator.configure( this.context );

        this.server.setHandler( this.context );
    }

    @Override
    public void start()
        throws Exception
    {
        this.server.start();
        this.server.join();
    }

    @Override
    public void stop()
        throws Exception
    {
        this.server.stop();
    }

    @Override
    public boolean isRunning()
    {
        return this.server.isRunning();
    }

    @Override
    public int getPort()
    {
        return this.connector.getPort();
    }

    @Override
    public void addFilter( final Filter filter )
    {

    }

    @Override
    public void removeFilter( final Filter filter )
    {

    }

    @Override
    public void addMapping( final FilterMapping mapping )
    {

    }

    @Override
    public void removeMapping( final FilterMapping mapping )
    {

    }

    @Override
    public void addServlet( final Servlet servlet )
    {

    }

    @Override
    public void removeServlet( final Servlet servlet )
    {

    }

    @Override
    public void addMapping( final ServletMapping mapping )
    {

    }

    @Override
    public void removeMapping( final ServletMapping mapping )
    {

    }
}
