package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketService;

@Component(immediate = true, service = {Servlet.class, WebSocketManager.class}, property = {"connector=xp"})
@Order( -100 )
@WebServlet("/admin/event")
public final class EventHandler
    extends HttpServlet
    implements WebSocketManager, EndpointFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( EventHandler.class );

    private static final String PROTOCOL = "text";

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    private WebSocketService webSocketService;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        if ( !req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() ) && !req.isUserInRole( RoleKeys.ADMIN.getId() ) )
        {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        if ( !this.webSocketService.isUpgradeRequest( req, res ) )
        {
            res.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        this.webSocketService.acceptWebSocket( req, res, this );
    }

    @Override
    public Endpoint newEndpoint()
    {
        return new EventWebSocket( this );
    }

    @Override
    public List<String> getSubProtocols()
    {
        return List.of( PROTOCOL );
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

    @Reference
    public void setWebSocketService( final WebSocketService webSocketService )
    {
        this.webSocketService = webSocketService;
    }
}
