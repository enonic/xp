package com.enonic.xp.portal.controller;

import java.util.function.Function;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
     * Runs {@code work} with every script execution on the calling thread confined to one script
     * context for the whole scope. {@code work} receives a view of this controller permanently
     * bound to that context: a connection opened by this scope (websocket/SSE) keeps the view,
     * and its events observe the module state the request's execution observed.
     */
    default <T extends @Nullable Object> T executeBound( Function<ControllerScript, T> work )
    {
        return work.apply( this );
    }

    /**
     * Declares the context this view is bound to in use by a live connection: while retained,
     * the context executes only through this view, keeping the connection's module state
     * undisturbed by unrelated requests. Reference-counted — pair every call with
     * {@link #release()}. Views not bound to a context ignore both calls.
     */
    default void retain()
    {
    }

    /**
     * Releases one {@link #retain()} reference; at zero the bound context becomes available to
     * other requests again.
     */
    default void release()
    {
    }
}
