package com.enonic.wem.core.module;

import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.CreateModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;

public class CreateModuleHandler
    extends CommandHandler<CreateModule>
{

    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter = new ModuleExporter();

    @Override
    public void handle()
        throws Exception
    {
        final ModuleKey moduleKey = ModuleKey.from( ModuleName.from( command.getName() ), command.getVersion() );
        final Module.Builder moduleBuilder = Module.newModule().
            moduleKey( moduleKey ).
            displayName( command.getDisplayName() ).
            info( command.getInfo() ).
            url( command.getUrl() ).
            vendorName( command.getVendorName() ).
            vendorUrl( command.getVendorUrl() ).
            config( command.getConfig() ).
            minSystemVersion( command.getMinSystemVersion() ).
            maxSystemVersion( command.getMaxSystemVersion() ).
            addContentTypeDependencies( command.getContentTypeDependencies() ).
            addModuleDependencies( command.getModuleDependencies() );

        final ModuleFileEntry moduleDirectoryEntry = command.getModuleDirectoryEntry();
        if ( moduleDirectoryEntry != null && moduleDirectoryEntry.isDirectory() && !moduleDirectoryEntry.isEmpty() )
        {
            final ModuleFileEntry.Builder moduleDirectoryBaseEntry = moduleBuilder.getModuleDirectoryEntry();
            for ( ModuleFileEntry fileEntry : moduleDirectoryEntry )
            {
                moduleDirectoryBaseEntry.addEntry( fileEntry );
            }
        }
        final Module module = moduleBuilder.build();

        final Path moduleDirPath = systemConfig.getModuleDir().toPath();

        moduleExporter.exportModuleToDirectory( module, moduleDirPath );
        command.setResult( module );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }
}
