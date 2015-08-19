package com.enonic.xp.script.impl.executor;

import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

public interface ScriptExecutor
{
    Object executeRequire( ResourceKey script );

    ScriptValue newScriptValue( Object value );

    ClassLoader getClassLoader();

    ServiceRegistry getServiceRegistry();

    ResourceService getResourceService();
}
