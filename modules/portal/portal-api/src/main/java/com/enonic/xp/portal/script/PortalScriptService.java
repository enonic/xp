package com.enonic.xp.portal.script;

import java.util.concurrent.CompletableFuture;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;


public interface PortalScriptService
{
    boolean hasScript( ResourceKey script );

    void bootstrap( ResourceKey mainScript );

    ScriptExports execute( ResourceKey script );

    /**
     * @deprecated Only {@code main.js} bootstrap used this, and it now runs synchronously through
     * {@link #bootstrap(ResourceKey)}; no caller remains. Scheduled for removal.
     */
    @Deprecated
    CompletableFuture<ScriptExports> executeAsync( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );
}
