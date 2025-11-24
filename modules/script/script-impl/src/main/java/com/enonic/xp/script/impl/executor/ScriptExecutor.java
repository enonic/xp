package com.enonic.xp.script.impl.executor;

import java.util.concurrent.CompletableFuture;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.runtime.ScriptSettings;

public interface ScriptExecutor
{
    Application getApplication();

    ScriptExports execute( ResourceKey key );

    CompletableFuture<ScriptExports> executeAsync( ResourceKey key );

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
