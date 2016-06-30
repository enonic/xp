package com.enonic.xp.portal.handler;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketConfig;

public interface PortalHandler
{
    int getOrder();

    boolean canHandle( PortalRequest req );

    PortalResponse handle( PortalRequest req )
        throws Exception;

    WebSocketEndpoint newWebSocketEndpoint( PortalRequest req, WebSocketConfig config )
        throws Exception;
}
