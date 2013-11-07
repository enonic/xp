package com.enonic.wem.core.module;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.command.module.DeleteModule;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class DeleteModuleHandler
    extends CommandHandler<DeleteModule>
{


    private SystemConfig systemConfig;

    @Override
    public void handle()
        throws Exception
    {
        final File modulesDir = systemConfig.getModulesDir();
        final File moduleDir = new File( modulesDir, command.getModule().toString() );
        boolean deleted = false;
        if ( moduleDir.exists() )
        {
            FileUtils.deleteDirectory( moduleDir );
            deleted = true;
        }
        command.setResult( deleted );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
