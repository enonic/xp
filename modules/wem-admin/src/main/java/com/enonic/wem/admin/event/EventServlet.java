package com.enonic.wem.admin.event;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventServlet
    extends WebSocketServlet
    implements WebSocketCreator, WebSocketManager
{

    private final static Logger LOG = LoggerFactory.getLogger( EventServlet.class );

    private static final String PROTOCOL = "text";

    private final Set<EventWebSocket> sockets = new CopyOnWriteArraySet<>();

    @Override
    public void configure( final WebSocketServletFactory factory )
    {
        factory.getPolicy().setIdleTimeout( TimeUnit.MINUTES.toMillis( 1 ) );
        factory.setCreator( this );
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
