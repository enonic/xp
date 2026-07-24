package com.enonic.xp.script.runtime;

import java.util.concurrent.CompletableFuture;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

public interface ScriptRuntime
{
    boolean hasScript( ResourceKey script );

    void bootstrap( BootstrapParams params );

    ScriptExports execute( ResourceKey script );

    /**
     * Executes one method of a script, synchronously on the calling thread. On pooled script
     * engines the call runs in a fresh private context, where the script's top level executes
     * lazily — no request-serving context is touched, and nothing is shared with any other call;
     * on engines without pooling it executes on the shared context. A missing script fails with
     * {@code ResourceNotFoundException}, uniformly across engines (use {@link #hasScript} to
     * validate ahead of time); a missing method fails loudly instead of silently doing nothing.
     * Nothing is returned by design: on pooled engines a result could not outlive the private
     * context. Named tasks run through this call.
     */
    void executeMethod( ResourceKey script, String method, Object... args );

    /**
     * @deprecated Only {@code main.js} bootstrap used this, and it now runs synchronously through
     * {@link #bootstrap(BootstrapParams)}; no caller remains. Scheduled for removal.
     */
    @Deprecated
    CompletableFuture<ScriptExports> executeAsync( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );

    void invalidate( ApplicationKey key );
}
