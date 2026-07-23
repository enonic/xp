package com.enonic.xp.script;

import java.util.function.Function;

import com.enonic.xp.resource.ResourceKey;


public interface ScriptExports
{
    ResourceKey getScript();

    ScriptValue getValue();

    boolean hasMethod( String name );

    ScriptValue executeMethod( String name, Object... args );

    Object getRawValue();

    /**
     * Runs {@code work} with one exclusively-held script context bound to the calling thread for
     * the whole scope: every execution inside — through these exports or nested ones — lands on
     * that exact context. {@code work} receives a view of these exports permanently pinned to it,
     * which may be retained beyond the scope so that later executions (e.g. the events of a
     * connection opened by this scope) run on the very context that ran the scope, preserving
     * its module state. Engines without context pooling simply pass {@code this}.
     */
    default <T> T executeBound( Function<ScriptExports, T> work )
    {
        return work.apply( this );
    }

    /**
     * Marks the context this view is pinned to as referenced by a long-lived consumer (e.g. an
     * open websocket or SSE connection): while referenced, the context is excluded from serving
     * unrelated executions, so the consumer's state and latency are not disturbed by the request
     * pool. Reference-counted — pair every call with {@link #release()}. No-op on views without
     * a pinned context and on engines without pooling.
     */
    default void retain()
    {
    }

    /**
     * Releases one {@link #retain()} reference; at zero the pinned context returns to the
     * general pool.
     */
    default void release()
    {
    }
}
