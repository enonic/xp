package com.enonic.wem.admin.event;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public final class EventServlet
    extends WebSocketServlet
    implements WebSocketManager
{
    private final static Logger LOG = LoggerFactory.getLogger( EventServlet.class );

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    @Override
    public WebSocket doWebSocketConnect( final HttpServletRequest request, final String protocol )
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
