package com.enonic.xp.portal.universalapi;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.EndpointFactory;
import com.enonic.xp.web.websocket.WebSocketEvent;

public interface UniversalApiHandler
{
    WebResponse handle( WebRequest request );

    default void onSocketEvent( WebSocketEvent event )
    {
    }

    default EndpointFactory getEndpointFactory()
    {
        return null;
    }
}
