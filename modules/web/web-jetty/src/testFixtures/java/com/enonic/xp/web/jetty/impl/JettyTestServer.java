package com.enonic.xp.web.jetty.impl;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.enonic.xp.web.dispatch.DispatchConstants;

public final class JettyTestServer
{
    private final Server server;

    private final ServletContextHandler handler;

    public JettyTestServer()
    {
        this.server = new Server();

        final ServerConnector connector = new ServerConnector( this.server, new HttpConnectionFactory() );
        connector.setHost( "localhost" );
        connector.setPort( 0 );
        connector.setName( DispatchConstants.XP_CONNECTOR );

        this.server.addConnector( connector );

        this.handler = new ServletContextHandler( null, "/", ServletContextHandler.SESSIONS );
        this.server.setHandler( this.handler );
    }

    public void start()
        throws Exception
    {
        this.server.start();
    }

    public void stop()
        throws Exception
    {
        this.server.stop();
    }

    public int getPort()
    {
        if ( this.server.isStarted() )
        {
            return ( (ServerConnector) this.server.getConnectors()[0] ).getLocalPort();
        }
        else
        {
            return -1;
        }
    }

    public ServletContextHandler getHandler()
    {
        return this.handler;
    }


    public void setVirtualHosts( final String[] hosts )
    {
        this.handler.setVirtualHosts( hosts );
    }

    public void addFilter( final Filter filter, final String mapping )
    {
        final FilterHolder holder = new FilterHolder();
        holder.setFilter( filter );

        this.handler.addFilter( holder, mapping, EnumSet.of( DispatcherType.REQUEST ) );
    }

    public void addServlet( final HttpServlet servlet, final String mapping )
    {
        final ServletHolder holder = new ServletHolder();
        holder.setServlet( servlet );

        this.handler.addServlet( holder, mapping );
    }
}