package com.enonic.xp.admin.event.impl;

public interface WebsocketManager
{
    void registerSocket( EventEndpoint eventEndpoint );

    void unregisterSocket( EventEndpoint eventEndpoint );

    void sendToAll( String message );
}
