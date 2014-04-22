package com.enonic.wem.core.script;

import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleResourceKeyResolver;

public interface ScriptContext
{
    public ModuleResourceKey getResourceKey();

    public ModuleKeyResolver getModuleKeyResolver();

    public ModuleResourceKeyResolver getResourceKeyResolver();
}
