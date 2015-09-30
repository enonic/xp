package com.enonic.xp.admin.event.impl;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public final class EventWebSocket
{
    private Session session;

    private final WebSocketManager webSocketManager;

    public EventWebSocket( final WebSocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }

    @OnWebSocketConnect
    public void onConnect( final Session session )
    {
        this.session = session;
        this.webSocketManager.registerSocket( this );
    }

    @OnWebSocketClose
    public void onClose( final Session session, final int statusCode, final String reason )
    {
        this.webSocketManager.unregisterSocket( this );
    }

    @OnWebSocketError
    public void onError( final Session session, final Throwable error )
    {
        this.webSocketManager.unregisterSocket( this );
    }

    public boolean isOpen()
    {
        return this.session.isOpen();
    }

    @OnWebSocketMessage
    public void onMessage( final Session session, final String message )
    {

    }

    public void sendMessage( final String message )
        throws IOException
    {
        if ( isOpen() )
        {
            session.getRemote().sendString( message );
        }
    }
}
