package com.enonic.xp.portal.controller;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.websocket.WebSocketEvent;

@NullMarked
public interface ControllerScript
{
    PortalResponse execute( PortalRequest portalRequest );

    default void onSocketEvent( WebSocketEvent event )
    {
    }

    default void onSseEvent( SseEvent event )
    {
    }
}
