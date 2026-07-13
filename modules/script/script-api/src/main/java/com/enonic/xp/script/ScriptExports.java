package com.enonic.xp.script;

import com.enonic.xp.resource.ResourceKey;


public interface ScriptExports
{
    ResourceKey getScript();

    ScriptValue getValue();

    boolean hasMethod( String name );

    ScriptValue executeMethod( String name, Object... args );

    Object getRawValue();

    /**
     * Returns a view of these exports whose executions have affinity to one underlying script
     * context, chosen deterministically from the given stable key (e.g. a websocket session id).
     * Repeated calls with an equal key select the same context, giving stateful consumers
     * per-connection ordering and module-state affinity on pooled script engines. Engines
     * without context pooling, or a {@code null} key, return {@code this}.
     */
    default ScriptExports pinned( Object affinityKey )
    {
        return this;
    }
}
