package com.enonic.xp.web.impl.websocket;

interface WebSocketEntry
{
    String getId();

    void addGroup( String group );

    void removeGroup( String group );

    void sendMessage( String message );

    boolean isInGroup( String group );
}
