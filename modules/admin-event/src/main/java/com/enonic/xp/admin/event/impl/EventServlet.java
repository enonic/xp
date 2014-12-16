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

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = {Servlet.class, WebSocketManager.class}, property = {"alias=/admin/event/*"})
public final class EventServlet
    extends HttpServlet
    implements WebSocketCreator, WebSocketManager
{
    private final static Logger LOG = LoggerFactory.getLogger( EventServlet.class );

    private static final String PROTOCOL = "text";

    private WebSocketServletFactory factory;

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    public void destroy()
    {
        this.factory.cleanup();
    }

    public void init()
        throws ServletException
    {
        try
        {
            final WebSocketPolicy webSocketPolicy = new WebSocketPolicy( WebSocketBehavior.SERVER );
            final WebSocketServletFactory baseFactory = new WebSocketServerFactory();
            this.factory = baseFactory.createFactory( webSocketPolicy );
            this.configure( this.factory );
            this.factory.init();
        }
        catch ( Exception e )
        {
            throw new ServletException( e );
        }
    }

    private void configure( final WebSocketServletFactory factory )
    {
        factory.getPolicy().setIdleTimeout( TimeUnit.MINUTES.toMillis( 1 ) );
        factory.setCreator( this );
    }

    protected void service( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        if ( this.factory.isUpgradeRequest( request, response ) )
        {
            if ( this.factory.acceptWebSocket( request, response ) )
            {
                return;
            }

            if ( response.isCommitted() )
            {
                return;
            }
        }

        super.service( request, response );
    }

    @Override
    public Object createWebSocket( final UpgradeRequest upgradeRequest, final UpgradeResponse upgradeResponse )
    {
        upgradeResponse.setAcceptedSubProtocol( PROTOCOL );
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
        for ( EventWebSocket eventWebSocket : this.sockets )
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
