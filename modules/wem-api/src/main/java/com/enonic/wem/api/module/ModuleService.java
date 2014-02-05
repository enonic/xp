package com.enonic.wem.api.module;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;

public interface ModuleService
{
    Module getModule( ModuleKey key )
        throws ModuleNotFoundException;

    Modules getModules( ModuleKeys keys );

    Modules getAllModules();

    Module deleteModule( ModuleKey key )
        throws ModuleNotFoundException;

    Module createModule( CreateModuleSpec spec );

    boolean updateModule( UpdateModuleSpec spec )
        throws ModuleNotFoundException;

    Resource getResource( ModuleResourceKey key )
        throws ModuleNotFoundException, ResourceNotFoundException;

    Resource createResource( CreateModuleResourceSpec spec )
        throws ModuleNotFoundException, ResourceNotFoundException;

}
