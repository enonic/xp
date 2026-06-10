package com.enonic.xp.web.jetty.impl;

import java.util.EnumSet;
import java.util.List;

import org.eclipse.jetty.ee11.servlet.FilterHolder;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.ee11.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.session.DefaultSessionIdManager;
import org.eclipse.jetty.session.HouseKeeper;
import org.eclipse.jetty.session.NullSessionCacheFactory;
import org.eclipse.jetty.session.SessionCache;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServlet;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.jetty.impl.websocket.WebSocketSessionTracker;

public final class JettyTestServer
{
    private final Server server;

    private final ServletContextHandler handler;

    private final WebSocketSessionTracker sessionTracker = new WebSocketSessionTracker();

    public JettyTestServer()
    {
        this( false );
    }

    public JettyTestServer( final boolean clusterStyleSessions )
    {
        this.server = new Server();

        final ServerConnector connector = new ServerConnector( this.server, new HttpConnectionFactory() );
        connector.setHost( "localhost" );
        connector.setPort( 0 );
        connector.setName( DispatchConstants.XP_CONNECTOR );

        this.server.addConnector( connector );

        // Mirror production wiring: idle-expired sessions are only invalidated when the scavenger runs.
        // Sweep every second so tests can observe expiry promptly.
        final DefaultSessionIdManager sessionIdManager = new DefaultSessionIdManager( this.server );
        final HouseKeeper houseKeeper = new HouseKeeper();
        try
        {
            houseKeeper.setIntervalSec( 1 );
        }
        catch ( final Exception e )
        {
            throw new IllegalStateException( e );
        }
        sessionIdManager.setSessionHouseKeeper( houseKeeper );
        this.server.addBean( sessionIdManager, true );

        this.handler = new ServletContextHandler( "/", ServletContextHandler.SESSIONS );
        if ( clusterStyleSessions )
        {
            // Mirror the Hazelcast wiring (HazelcastSessionStoreFactoryActivator): no session cache, so each
            // request works on its own ManagedSession object that goes non-resident at request completion,
            // and sessions survive only in the shared data store.
            final SessionCache sessionCache = new NullSessionCacheFactory().getSessionCache( this.handler.getSessionHandler() );
            sessionCache.setSessionDataStore( new InMemorySessionDataStore() );
            this.handler.getSessionHandler().setSessionCache( sessionCache );
        }
        this.handler.addEventListener( this.sessionTracker );
        JakartaWebSocketServletContainerInitializer.configure( this.handler, ( _, _ ) -> {
        } );
        this.server.setHandler( this.handler );
    }

    public WebSocketSessionTracker getSessionTracker()
    {
        return this.sessionTracker;
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


    public void setVirtualHosts( final List<String> hosts )
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
