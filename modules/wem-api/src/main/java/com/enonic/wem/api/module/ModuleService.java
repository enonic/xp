package com.enonic.wem.api.module;

public interface ModuleService
{
    public Module delete( ModuleKey key )
        throws ModuleNotFoundException;
}
