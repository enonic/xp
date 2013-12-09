package com.enonic.wem.core.module;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.GetModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.exporters.ModuleExporter;

public class GetModuleHandler
    extends CommandHandler<GetModule>
{

    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    @Override
    public void handle()
        throws Exception
    {
        final ModuleKey moduleKey = command.getModuleKey();
        final Path moduleDir = moduleResourcePathResolver.resolveModulePath( moduleKey );
        if ( Files.exists( moduleDir ) )
        {
            final Module module = moduleExporter.importFromDirectory( moduleDir ).build();
            command.setResult( module );
            return;
        }

        throw new ModuleNotFoundException( moduleKey );
    }


    @Inject
    public void setModuleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
    }

    @Inject
    public void setModuleImporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
    }
}
