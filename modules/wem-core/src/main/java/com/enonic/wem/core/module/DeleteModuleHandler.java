package com.enonic.wem.core.module;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.command.module.DeleteModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

public class DeleteModuleHandler
    extends CommandHandler<DeleteModule>
{
    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter;

    @Override
    public void handle()
        throws Exception
    {
        final File modulesDir = systemConfig.getModulesDir();
        final File moduleDir = new File( modulesDir, command.getModule().toString() );
        if ( !moduleDir.exists() )
        {
            throw new ModuleNotFoundException( command.getModule() );
        }

        final Module module = moduleExporter.importFromDirectory( moduleDir.toPath() ).build();
        FileUtils.deleteDirectory( moduleDir );

        command.setResult( module );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }


    @Inject
    public void setModuleImporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
    }
}
