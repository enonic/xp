package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.security.RoleKeys;

@Component(immediate = true, service = {Servlet.class, WebSocketManager.class},
    property = {"osgi.http.whiteboard.servlet.pattern=/admin/event"})
public final class EventHandler
    extends HttpServlet
    implements WebSocketCreator, WebSocketManager
{
    private final static Logger LOG = LoggerFactory.getLogger( EventHandler.class );

    private static final String PROTOCOL = "text";

    private WebSocketServletFactory factory;

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    protected boolean securityEnabled = true;

    @Override
    public void init()
        throws ServletException
    {
        final WebSocketPolicy policy = new WebSocketPolicy( WebSocketBehavior.SERVER );
        this.factory = new WebSocketServerFactory().createFactory( policy );
        configure( this.factory );

        try
        {
            this.factory.init( getServletContext() );
        }
        catch ( final Exception e )
        {
            throw new ServletException( e );
        }
    }

    @Override
    public void destroy()
    {
        this.factory.cleanup();
    }

    private void configure( final WebSocketServletFactory factory )
    {
        factory.getPolicy().setIdleTimeout( TimeUnit.MINUTES.toMillis( 10 ) );
        factory.setCreator( this );
    }

    @Override
    protected void service( HttpServletRequest req, HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !this.factory.isUpgradeRequest( req, res ) )
        {
            return;
        }

        if ( this.securityEnabled && !req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() ) )
        {
            res.setStatus( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        this.factory.acceptWebSocket( req, res );
    }

    @Override
    public Object createWebSocket( final ServletUpgradeRequest req, final ServletUpgradeResponse res )
    {
        res.setAcceptedSubProtocol( PROTOCOL );
        return new EventWebSocket( this );
    }

    @Override
    public void registerSocket( final EventWebSocket webSocket )
    {
        this.sockets.add( webSocket );
    }

    @Override
    public void unregisterSocket( final EventWebSocket webSocket )
    {
        this.sockets.remove( webSocket );
    }

    @Override
    public void sendToAll( final String message )
    {
        for ( final EventWebSocket eventWebSocket : this.sockets )
        {
            try
            {
                eventWebSocket.sendMessage( message );
            }
            catch ( IOException e )
            {
                LOG.warn( "Failed to send message via web socket", e );
            }
        }
    }
}
