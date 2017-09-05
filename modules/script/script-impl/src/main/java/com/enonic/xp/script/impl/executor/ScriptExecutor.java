package com.enonic.xp.script.impl.executor;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.runtime.ScriptSettings;

public interface ScriptExecutor
{
    Application getApplication();

    ScriptExports executeMain( ResourceKey key );

    Object executeRequire( ResourceKey key );

    ScriptValue newScriptValue( Object value );

    ClassLoader getClassLoader();

    ServiceRegistry getServiceRegistry();

    ResourceService getResourceService();

    ScriptSettings getScriptSettings();

    JavascriptHelper getJavascriptHelper();

    void registerMock( String name, Object value );

    void registerDisposer( final ResourceKey key, Runnable callback );

    void runDisposers();
}
