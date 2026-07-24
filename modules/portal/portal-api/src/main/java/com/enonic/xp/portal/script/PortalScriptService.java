package com.enonic.xp.portal.script;

import java.util.concurrent.CompletableFuture;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.runtime.BootstrapParams;


public interface PortalScriptService
{
    boolean hasScript( ResourceKey script );

    void bootstrap( BootstrapParams params );

    ScriptExports execute( ResourceKey script );

    /**
     * Executes one script method in the background execution model. On pooled script engines the
     * call runs in a fresh private context, where the script's top level executes lazily — no
     * request-serving context is touched, and nothing is shared with any other call. A missing
     * script fails with {@code ResourceNotFoundException}, uniformly across engines (use
     * {@link #hasScript} to validate ahead of time); a missing method fails loudly — a background
     * run has no caller to observe a silent no-op. Nothing is returned by design: a result could
     * never outlive the private context. On engines without pooling the call executes on the
     * shared context.
     */
    void executeBackground( ResourceKey script, String method, Object... args );

    /**
     * @deprecated Only {@code main.js} bootstrap used this, and it now runs synchronously through
     * {@link #bootstrap(BootstrapParams)}; no caller remains. Scheduled for removal.
     */
    @Deprecated
    CompletableFuture<ScriptExports> executeAsync( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );
}
