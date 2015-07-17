package com.enonic.xp.app;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleNotFoundException;

@Beta
public interface ApplicationService
{
    Module getModule( ApplicationKey key )
        throws ModuleNotFoundException;

    Applications getModules( ApplicationKeys keys );

    Applications getAllModules();

    ClassLoader getClassLoader(Module module);

    void startModule( ApplicationKey key );

    void stopModule( ApplicationKey key );
}
