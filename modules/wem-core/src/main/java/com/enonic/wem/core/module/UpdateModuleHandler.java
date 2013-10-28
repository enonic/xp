package com.enonic.wem.core.module;

import java.io.File;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.UpdateModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class UpdateModuleHandler
    extends CommandHandler<UpdateModule>
{

    private SystemConfig systemConfig;

    private ModuleImporter moduleImporter;

    private ModuleExporter moduleExporter;

    @Override
    public void handle()
        throws Exception
    {
        final File modulesDir = systemConfig.getModuleDir();
        final File moduleDir = new File( modulesDir, command.getModuleKey().toString() );
        if ( !moduleDir.exists() )
        {
            throw new ModuleNotFoundException( command.getModuleKey() );
        }

        Module module = moduleImporter.importModuleFromDirectory( moduleDir.toPath() );
        Module editedModule = command.getEditor().edit( module );
        boolean edited = editedModule != null && !editedModule.equals( module );
        if ( edited )
        {
            moduleExporter.exportModuleToDirectory( editedModule, modulesDir.toPath() );
        }

        command.setResult( edited );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Inject
    public void setModuleImporter( final ModuleImporter moduleImporter )
    {
        this.moduleImporter = moduleImporter;
    }

    @Inject
    public void setModuleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
    }
}
