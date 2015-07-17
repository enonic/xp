package com.enonic.xp.module;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;

@Beta
public interface ModuleService
{
    Module getModule( ApplicationKey key )
        throws ModuleNotFoundException;

    Modules getModules( ApplicationKeys keys );

    Modules getAllModules();

    ClassLoader getClassLoader(Module module);

    void startModule( ApplicationKey key );

    void stopModule( ApplicationKey key );
}
