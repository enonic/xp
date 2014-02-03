package com.enonic.wem.api.module;

public interface ModuleService
{
    public Module getModule( ModuleKey key )
        throws ModuleNotFoundException;

    public Modules getModules( ModuleKeys keys );

    public Modules getAllModules();

    public Module deleteModule( ModuleKey key )
        throws ModuleNotFoundException;

    public Module createModule( CreateModuleSpec spec );

    public boolean updateModule( UpdateModuleSpec spec )
        throws ModuleNotFoundException;
}
