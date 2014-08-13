package com.enonic.wem.admin.event;

public interface WebSocketManager2
{

    void registerSocket( EventWebSocket webSocket );

    void unregisterSocket( EventWebSocket webSocket );

    void sendToAll( String message );
}
