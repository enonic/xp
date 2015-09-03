package com.enonic.xp.web.impl;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public final class JettyTestServer
{
    private final Server server;

    private final ServletHandler handler;

    public JettyTestServer()
    {
        this.server = new Server( 0 );
        this.handler = new ServletHandler();
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

    public void addServlet( final HttpServlet servlet, final String mapping )
    {
        final ServletHolder holder = new ServletHolder();
        holder.setServlet( servlet );

        this.handler.addServletWithMapping( holder, mapping );
    }
}
