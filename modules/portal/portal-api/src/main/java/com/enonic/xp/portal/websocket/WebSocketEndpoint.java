package com.enonic.xp.portal.websocket;

public interface WebSocketEndpoint
{
    WebSocketConfig getConfig();

    void onEvent( WebSocketEvent event );
}
