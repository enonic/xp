package com.enonic.xp.portal.script;

import java.util.concurrent.CompletableFuture;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.BackgroundScript;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.runtime.BootstrapParams;


public interface PortalScriptService
{
    boolean hasScript( ResourceKey script );

    void bootstrap( BootstrapParams params );

    ScriptExports execute( ResourceKey script );

    /**
     * Resolves a script for background execution. On pooled script engines the returned view is
     * bound to no context: each method invocation runs in a fresh private context, where the
     * script's top level executes lazily, and no request-serving context is used to obtain the
     * view. A missing script fails here with {@code ResourceNotFoundException}, uniformly across
     * engines; the script is additionally initialized asynchronously (once per executor
     * incarnation), so a broken script appears in the logs even if the view is never invoked.
     * On engines without pooling invocations execute on the shared context.
     */
    BackgroundScript executeBackground( ResourceKey script );

    /**
     * @deprecated Only {@code main.js} bootstrap used this, and it now runs synchronously through
     * {@link #bootstrap(BootstrapParams)}; no caller remains. Scheduled for removal.
     */
    @Deprecated
    CompletableFuture<ScriptExports> executeAsync( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );
}
