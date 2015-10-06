package com.enonic.xp.script.impl.executor;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeyResolver;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.runtime.ScriptSettings;

public interface ScriptExecutor
{
    Application getApplication();

    Object executeRequire( ResourceKey script );

    ScriptValue newScriptValue( Object value );

    ClassLoader getClassLoader();

    ServiceRegistry getServiceRegistry();

    ResourceService getResourceService();

    ScriptSettings getScriptSettings();

    ResourceKeyResolver getResourceKeyResolver();
}
