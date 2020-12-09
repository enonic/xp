package com.enonic.xp.portal.websocket;

public interface WebSocketManager
{
    void send( String id, String message );

    void sendToGroup( String group, String message );

    long getGroupSize( String group );

    void addToGroup( String group, String id );

    void removeFromGroup( String group, String id );
}
