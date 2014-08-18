package com.enonic.wem.admin.event;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket;

public final class EventWebSocket
    implements WebSocket.OnTextMessage
{
    private WebSocket.Connection connection;

    private final WebSocketManager webSocketManager;

    public EventWebSocket( final WebSocketManager webSocketManager )
    {
        this.webSocketManager = webSocketManager;
    }

    @Override
    public void onOpen( final WebSocket.Connection connection )
    {
        this.connection = connection;
        this.webSocketManager.registerSocket( this );
    }

    @Override
    public void onClose( final int closeCode, final String message )
    {
        this.webSocketManager.unregisterSocket( this );
    }

    public boolean isOpen()
    {
        return this.connection.isOpen();
    }

    @Override
    public void onMessage( final String message )
    {

    }

    public void sendMessage( final String message )
        throws IOException
    {
        if ( isOpen() )
        {
            this.connection.sendMessage( message );
        }
    }
}
