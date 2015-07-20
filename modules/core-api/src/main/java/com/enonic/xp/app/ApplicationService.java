package com.enonic.xp.app;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleNotFoundException;
import com.enonic.xp.module.Modules;

@Beta
public interface ApplicationService
{
    Module getModule( ApplicationKey key )
        throws ModuleNotFoundException;

    Modules getModules( ApplicationKeys keys );

    Modules getAllModules();

    ClassLoader getClassLoader(Module module);

    void startModule( ApplicationKey key );

    void stopModule( ApplicationKey key );
}
