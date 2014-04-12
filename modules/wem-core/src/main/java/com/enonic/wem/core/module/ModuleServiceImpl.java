package com.enonic.wem.core.module;

import javax.inject.Inject;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
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
}
