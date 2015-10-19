package com.enonic.xp.admin.event.impl;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Endpoint;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.websocket.BaseWebSocketHandler;
import com.enonic.xp.web.websocket.WebSocketHandler;

@Component(immediate = true, service = {WebSocketHandler.class, WebSocketManager.class})
public final class EventHandler
    extends BaseWebSocketHandler
    implements WebSocketManager
{
    private final static Logger LOG = LoggerFactory.getLogger( EventHandler.class );

    private static final String PROTOCOL = "text";

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    public EventHandler()
    {
        setPath( "/admin/event" );
        setSubProtocols( PROTOCOL );
    }

    @Override
    public boolean hasAccess( final HttpServletRequest req )
    {
        return req.isUserInRole( RoleKeys.ADMIN_LOGIN.getId() );
    }

    @Override
    public Endpoint newEndpoint()
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
}

