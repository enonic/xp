package com.enonic.wem.api.module;

public interface ModuleService
{
    public Module getModule( ModuleKey key )
        throws ModuleNotFoundException;

    public Module deleteModule( ModuleKey key )
        throws ModuleNotFoundException;
}
