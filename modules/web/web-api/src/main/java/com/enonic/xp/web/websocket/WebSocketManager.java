package com.enonic.xp.web.websocket;

public interface WebSocketManager
{
    void send( String id, String message );

    void sendToGroup( String group, String message );

    void addToGroup( String group, String id );

    void removeFromGroup( String group, String id );
}
