package com.enonic.xp.script;

import java.util.function.Function;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.resource.ResourceKey;


@NullMarked
public interface ScriptExports
{
    ResourceKey getScript();

    @Nullable ScriptValue getValue();

    boolean hasMethod( String name );

    @Nullable ScriptValue executeMethod( String name, @Nullable Object... args );

    @Nullable Object getRawValue();

    /**
     * Runs {@code work} with every script execution on the calling thread — through these exports
     * or nested ones — confined to one script context for the whole scope. {@code work} receives
     * a view of these exports permanently bound to that context: executions through the view,
     * inside or after the scope, observe the module state the scope's executions observed.
     */
    default <T extends @Nullable Object> T executeBound( Function<ScriptExports, T> work )
    {
        return work.apply( this );
    }

    /**
     * Declares the context this view is bound to in use by a long-lived consumer (such as an open
     * websocket or SSE connection): while retained, the context executes only through this view,
     * keeping the consumer's module state undisturbed by unrelated executions. Reference-counted —
     * pair every call with {@link #release()}. Views not bound to a context ignore both calls.
     */
    default void retain()
    {
    }

    /**
     * Releases one {@link #retain()} reference; at zero the bound context becomes available to
     * other executions again.
     */
    default void release()
    {
    }
}
