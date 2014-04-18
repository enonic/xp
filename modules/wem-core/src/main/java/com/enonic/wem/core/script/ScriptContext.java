package com.enonic.wem.core.script;

import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeyResolver;

public interface ScriptContext
{
    public ResourceKey getResourceKey();

    public ModuleKeyResolver getModuleKeyResolver();

    public ResourceKeyResolver getResourceKeyResolver();
}
