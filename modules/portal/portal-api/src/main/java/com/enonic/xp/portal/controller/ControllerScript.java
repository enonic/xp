package com.enonic.xp.portal.controller;

import java.util.function.Function;

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
     * Runs {@code work} with one exclusively-held script context bound to the calling thread for
     * the whole scope. {@code work} receives a view of this controller permanently pinned to
     * that exact context; retaining it lets a connection opened by this scope (websocket/SSE)
     * dispatch all its events to the very context that executed the request, preserving its
     * module state. Engines without context pooling simply pass {@code this}.
     */
    default <T> T executeBound( Function<ControllerScript, T> work )
    {
        return work.apply( this );
    }

    /**
     * Marks the context this view is pinned to as referenced by a live connection: while
     * referenced it is excluded from serving unrelated requests. Reference-counted — pair every
     * call with {@link #release()}. No-op on unpinned views and engines without pooling.
     */
    default void retain()
    {
    }

    /**
     * Releases one {@link #retain()} reference; at zero the pinned context returns to the
     * request pool.
     */
    default void release()
    {
    }
}
