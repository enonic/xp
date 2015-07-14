package com.enonic.xp.module;

import com.google.common.annotations.Beta;

@Beta
public interface ModuleService
{
    Module getModule( ModuleKey key )
        throws ModuleNotFoundException;

    Modules getModules( ModuleKeys keys );

    Modules getAllModules();

    ClassLoader getClassLoader(Module module);

    void startModule( ModuleKey key );

    void stopModule( ModuleKey key );
}
