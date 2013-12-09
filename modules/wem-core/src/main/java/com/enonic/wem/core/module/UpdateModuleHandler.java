package com.enonic.wem.core.module;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.UpdateModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

public class UpdateModuleHandler
    extends CommandHandler<UpdateModule>
{

    private SystemConfig systemConfig;

    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    @Override
    public void handle()
        throws Exception
    {
        final Path modulesDir = systemConfig.getModulesDir();
        final Path moduleDir = moduleResourcePathResolver.resolveModulePath( command.getModuleKey() );
        if ( Files.notExists( moduleDir ) )
        {
            throw new ModuleNotFoundException( command.getModuleKey() );
        }

        Module module = moduleExporter.importFromDirectory( moduleDir ).build();
        Module editedModule = command.getEditor().edit( module );
        boolean edited = editedModule != null && !editedModule.equals( module );
        if ( edited )
        {
            moduleExporter.exportToDirectory( editedModule, modulesDir );
        }

        command.setResult( edited );
    }

    @Inject
    public void setModuleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Inject
    public void setModuleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
    }
}
