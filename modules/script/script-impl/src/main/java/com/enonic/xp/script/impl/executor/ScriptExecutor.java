package com.enonic.xp.script.impl.executor;

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

    /**
     * Executes one method of a script, synchronously on the calling thread. On pooled engines
     * the call runs in a fresh private context where the script's top level executes lazily — no
     * request-serving slot is touched, and nothing is shared with any other call. Engines
     * without pooling execute on the shared context. Unlike {@code ScriptExports.executeMethod},
     * a missing method fails loudly instead of answering {@code null}. Returns the result when
     * it is a scalar, {@code null} otherwise — uniformly on every engine.
     */
    Object executeMethod( ResourceKey key, String method, Object... args );

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
