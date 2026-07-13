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

    /**
     * Returns a view of this controller whose script executions have affinity to one underlying
     * script context, chosen deterministically from the given stable key (e.g. a websocket
     * session id or SSE client id). Gives per-connection event ordering and module-state
     * affinity on pooled script engines. Engines without context pooling return {@code this}.
     */
    default ControllerScript pinned( Object affinityKey )
    {
        return this;
    }
}
