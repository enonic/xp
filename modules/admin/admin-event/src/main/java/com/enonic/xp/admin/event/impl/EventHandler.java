package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.websocket.WebSocketHandler;
import com.enonic.xp.web.websocket.WebSocketHandlerFactory;
import com.enonic.xp.web.websocket.WebSocketServlet;

@Component(immediate = true, service = {Servlet.class, WebSocketManager.class},
    property = {"osgi.http.whiteboard.servlet.pattern=/admin/event"})
public final class EventHandler
    extends WebSocketServlet
    implements WebSocketManager
{
    private final static Logger LOG = LoggerFactory.getLogger( EventHandler.class );

    private static final String PROTOCOL = "text";

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    @Override
    protected void configure( final WebSocketHandler handler )
        throws Exception
    {
        handler.setEndpointProvider( this::newEndpoint );
        handler.setDefaultMaxSessionIdleTimeout( TimeUnit.MINUTES.toMillis( 10 ) );
        handler.addSubProtocol( PROTOCOL );
    }

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() ) )
        {
            res.setStatus( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        super.service( req, res );
    }

    protected EventWebSocket newEndpoint()
    {
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
        for ( final EventWebSocket socket : this.sockets )
        {
            try
            {
                socket.sendMessage( message );
            }
            catch ( IOException e )
            {
                LOG.warn( "Failed to send message via web socket", e );
            }
        }
    }

    @Override
    @Reference
    public void setHandlerFactory( final WebSocketHandlerFactory handlerFactory )
    {
        super.setHandlerFactory( handlerFactory );
    }
}
