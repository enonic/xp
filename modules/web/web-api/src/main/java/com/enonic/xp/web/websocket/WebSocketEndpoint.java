package com.enonic.xp.web.websocket;

public interface WebSocketEndpoint
{
    WebSocketConfig getConfig();

    void onEvent( WebSocketEvent event );
}
