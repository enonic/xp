package com.enonic.xp.admin.event.impl;

public interface WebSocketManager
{
    void registerSocket( EventWebSocket webSocket );

    void unregisterSocket( EventWebSocket webSocket );

    void sendToAll( String message );
}
