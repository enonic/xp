package com.enonic.wem.core.module;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.GetModules;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class GetModulesHandler
    extends CommandHandler<GetModules>
{

    private SystemConfig systemConfig;

    private ModuleImporter moduleImporter;

    @Override
    public void handle()
        throws Exception
    {
        final File modulesDir = systemConfig.getModuleDir();
        final ModuleKeys moduleKeys = command.getModules();

        List<Module> modules = new ArrayList<>();
        for ( ModuleKey moduleKey : moduleKeys )
        {
            final File moduleDir = new File( modulesDir, moduleKey.toString() );
            if ( moduleDir.exists() && moduleDir.isDirectory() )
            {
                Module module = moduleImporter.importModuleFromDirectory( moduleDir.toPath() );
                modules.add( module );
            }
        }

        command.setResult( Modules.from( modules ) );
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
}
