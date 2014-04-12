package com.enonic.wem.api.module;

public interface ModuleService
{
    Module getModule( ModuleKey key )
        throws ModuleNotFoundException;

    Modules getModules( ModuleKeys keys );

    Modules getAllModules();

    Module deleteModule( ModuleKey key )
        throws ModuleNotFoundException;
}
