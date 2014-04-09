package com.enonic.wem.core.module;

import javax.inject.Inject;

import com.enonic.wem.api.module.CreateModuleParams;
import com.enonic.wem.api.module.CreateModuleResourceParams;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceNotFoundException;
import com.enonic.wem.core.config.SystemConfig;

public final class ModuleServiceImpl
    implements ModuleService
{
    @Inject
    protected ModuleResourcePathResolver moduleResourcePathResolver;

    @Inject
    protected ModuleExporter moduleExporter;

    @Inject
    protected SystemConfig systemConfig;

    @Override
    public Module getModule( final ModuleKey key )
        throws ModuleNotFoundException
    {
        return new GetModuleCommand().key( key ).moduleExporter( this.moduleExporter ).moduleResourcePathResolver(
            this.moduleResourcePathResolver ).execute();
    }

    @Override
    public Module deleteModule( final ModuleKey key )
        throws ModuleNotFoundException
    {
        return new DeleteModuleCommand().key( key ).moduleExporter( this.moduleExporter ).moduleResourcePathResolver(
            this.moduleResourcePathResolver ).execute();
    }

    @Override
    public Modules getModules( final ModuleKeys keys )
    {
        return new GetModulesCommand().keys( keys ).moduleExporter( this.moduleExporter ).moduleResourcePathResolver(
            this.moduleResourcePathResolver ).execute();
    }

    @Override
    public Modules getAllModules()
    {
        return new GetAllModulesCommand().moduleExporter( this.moduleExporter ).systemConfig( this.systemConfig ).execute();
    }

    @Override
    public Module createModule( final CreateModuleParams params )
    {
        return new CreateModuleCommand().params( params ).moduleExporter( this.moduleExporter ).systemConfig( this.systemConfig ).execute();
    }

    @Override
    public Resource getResource( final ModuleResourceKey key )
        throws ModuleNotFoundException, ResourceNotFoundException
    {
        return new GetModuleResourceCommand().key( key ).moduleResourcePathResolver( this.moduleResourcePathResolver ).execute();
    }

    @Override
    public Resource createResource( final CreateModuleResourceParams params )
        throws ModuleNotFoundException, ResourceNotFoundException
    {
        return new CreateModuleResourceCommand().params( params ).moduleResourcePathResolver( this.moduleResourcePathResolver ).execute();
    }
}
