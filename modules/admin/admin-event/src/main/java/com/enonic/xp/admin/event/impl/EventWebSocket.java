package com.enonic.xp.admin.event.impl;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

public final class EventWebSocket
    extends Endpoint
{
    private Session session;

    private final WebSocketManager webSocketManager;

    public EventWebSocket( final WebSocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void onOpen( final Session session, final EndpointConfig config )
    {
        this.session = session;
        this.webSocketManager.registerSocket( this );
    }

    @Override
    public void onClose( final Session session, final CloseReason closeReason )
    {
        this.webSocketManager.unregisterSocket( this );
        this.session = null;
    }

    public void onError( final Session session, final Throwable error )
    {
        this.webSocketManager.unregisterSocket( this );
        this.session = null;
    }

    public boolean isOpen()
    {
        return ( this.session != null ) && this.session.isOpen();
    }

    public void sendMessage( final String message )
        throws IOException
    {
        if ( isOpen() )
        {
            this.session.getBasicRemote().sendText( message );
        }
    }
}
