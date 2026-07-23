package com.enonic.xp.script.impl.executor;

import java.util.concurrent.CompletableFuture;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.runtime.ScriptSettings;

public interface ScriptExecutor
{
    ScriptExports executeMain( ResourceKey key );

    /**
     * Runs the application's bootstrap script on the dedicated main context (not a pooled request
     * context), so its listeners and disposers share that context. The caller — not the script's
     * name — decides that this is the bootstrap.
     */
    ScriptExports bootstrap( ResourceKey key );

    CompletableFuture<ScriptExports> executeMainAsync( ResourceKey key );

    /**
     * Exports view for background execution. On pooled engines the view is bound to no context —
     * each invocation runs in a fresh private context where the script's top level executes lazily,
     * and no request-serving slot is touched to obtain it. Engines without pooling return the same
     * view as {@link #executeMain}.
     */
    ScriptExports backgroundExports( ResourceKey key );

    /**
     * Whether this executor's engine pools contexts: concurrent executions run on separate
     * contexts and {@link #backgroundExports} views are isolated (a fresh private context per
     * invocation). Engines without pooling share one context for everything.
     */
    boolean isPooled();

    Object executeRequire( ResourceKey key );

    ScriptValue newScriptValue( Object value );

    ClassLoader getClassLoader();

    ServiceRegistry getServiceRegistry();

    ResourceService getResourceService();

    ScriptSettings getScriptSettings();

    ObjectConverter getObjectConverter();

    void registerMock( String name, Object value );

    void registerDisposer( ResourceKey key, Runnable callback );

    void runDisposers();
}
