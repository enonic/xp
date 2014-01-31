package com.enonic.wem.core.module;

import javax.inject.Inject;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;

public final class ModuleServiceImpl
    implements ModuleService
{
    @Inject
    protected ModuleResourcePathResolver moduleResourcePathResolver;

    @Inject
    protected ModuleExporter moduleExporter;

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
}
