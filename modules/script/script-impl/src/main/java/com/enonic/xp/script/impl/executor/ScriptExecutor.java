package com.enonic.xp.script.impl.executor;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.BackgroundScript;
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
     * One script method resolved for background execution. On pooled engines the view is bound
     * to no context — each invocation runs in a fresh private context where the script's top
     * level executes lazily, and no request-serving slot is touched to obtain it. Engines
     * without pooling execute on the shared context. Unlike {@code ScriptExports.executeMethod},
     * invoking a missing method fails loudly — a background run has no caller to observe a
     * silent no-op.
     */
    BackgroundScript backgroundExports( ResourceKey key, String method );

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
