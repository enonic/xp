package com.enonic.xp.module;

public interface ModuleService
{
    Module getModule( ModuleKey key )
        throws ModuleNotFoundException;

    Modules getModules( ModuleKeys keys );

    Modules getAllModules();
}
