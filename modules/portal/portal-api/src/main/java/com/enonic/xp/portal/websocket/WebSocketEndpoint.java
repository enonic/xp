package com.enonic.xp.portal.websocket;

import com.enonic.xp.web.websocket.WebSocketConfig;

public interface WebSocketEndpoint
{
    WebSocketConfig getConfig();

    void onEvent( WebSocketEvent event );
}
