package com.enonic.xp.portal.universalapi;

import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketEvent;

public interface UniversalApiHandler
{
    WebResponse handle( WebRequest request );

    default void onSocketEvent( WebSocketEvent event )
    {
    }

    default void onSseEvent( SseEvent event )
    {
    }
}
