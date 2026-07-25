package com.enonic.xp.script.runtime;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

public interface ScriptRuntime
{
    boolean hasScript( ResourceKey script );

    /**
     * Bootstraps an application: runs its optional bootstrap script and marks the application
     * ready — top-level executions of the application's scripts wait for that. Only the first
     * call per application incarnation runs the script; concurrent and later calls wait for or
     * observe the completed bootstrap. A failing bootstrap script is logged, and the application
     * still becomes ready.
     */
    @NullMarked
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
    @NullMarked
    @Nullable Object executeMethod( ResourceKey script, String method, @Nullable Object... args );

    /**
     * @deprecated Scheduled for removal. Use {@link #bootstrap(BootstrapParams)} for bootstrap
     * scripts, or {@link #execute(ResourceKey)} on an executor of the caller's choice when
     * asynchrony is needed.
     */
    @Deprecated
    CompletableFuture<ScriptExports> executeAsync( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );

    void invalidate( ApplicationKey key );
}
