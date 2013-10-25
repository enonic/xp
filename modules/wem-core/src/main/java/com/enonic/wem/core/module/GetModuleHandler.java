package com.enonic.wem.core.module;

import java.io.File;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.GetModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class GetModuleHandler
    extends CommandHandler<GetModule>
{

    private SystemConfig systemConfig;

    private ModuleImporter moduleImporter;

    @Override
    public void handle()
        throws Exception
    {
        final File modulesDir = systemConfig.getModuleDir();
        final ModuleKey moduleKey = command.getModuleKey();
        final File moduleDir = new File( modulesDir, moduleKey.toString() );
        if ( moduleDir.exists() )
        {
            Module module = moduleImporter.importModuleFromDirectory( moduleDir.toPath() );
            command.setResult( module );
            return;
        }

        throw new ModuleNotFoundException( moduleKey );
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
