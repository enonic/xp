package com.enonic.xp.portal.impl.websocket;

interface WebSocketEntry
{
    String getId();

    void addGroup( String group );

    void removeGroup( String group );

    void sendMessage( String message );

    boolean isInGroup( String group );
}
