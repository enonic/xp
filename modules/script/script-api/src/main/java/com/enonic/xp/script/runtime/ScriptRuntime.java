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
     * Resolves a script's exports for background execution. On pooled script engines the returned
     * view is bound to no context: each method invocation runs in a fresh private context, where
     * the script's top level executes lazily, and no request-serving context is used to obtain
     * the view. The script is additionally initialized asynchronously (once per executor
     * incarnation), so a missing or broken script appears in the logs even if the view is never
     * invoked — callers still see the error on their first invocation. On engines without
     * pooling this is equivalent to {@link #execute}.
     */
    ScriptExports executeBackground( ResourceKey script );

    /**
     * Whether the application's script engine pools contexts. On a pooled engine concurrent
     * executions run on separate contexts and {@link #executeBackground} is isolated — each
     * invocation runs in a fresh private context, so functions cannot carry closures across. On
     * an engine without pooling everything shares one context and background execution is
     * attached.
     */
    boolean isPooled( ApplicationKey application );

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
