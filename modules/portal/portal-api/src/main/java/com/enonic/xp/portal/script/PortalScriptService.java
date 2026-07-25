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
     * Executes one exported method of a script, synchronously on the calling thread. Each call is
     * independent: module state must not be expected to be shared with any other execution or to
     * carry over between calls. Fails with {@code ResourceNotFoundException} when the script does
     * not exist (use {@link #hasScript} to validate ahead of time) and with
     * {@code IllegalArgumentException} when the script does not export the method.
     *
     * @return the method's result when it is a scalar (string, number, boolean or date), and
     * {@code null} for any other result — identically on every script engine.
     */
    Object executeMethod( ResourceKey script, String method, Object... args );

    /**
     * @deprecated Scheduled for removal. Use {@link #bootstrap(BootstrapParams)} for bootstrap
     * scripts, or {@link #execute(ResourceKey)} on an executor of the caller's choice when
     * asynchrony is needed.
     */
    @Deprecated
    CompletableFuture<ScriptExports> executeAsync( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );
}
