package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.CreateModuleParams;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.util.Exceptions;

final class CreateModuleCommand
{
    private CreateModuleParams params;

    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter;

    public Module execute()
    {
        this.params.validate();
        
        try
        {
            return doExecute();
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRutime( "Error creating module" ).withCause( e );
        }
    }

    private Module doExecute()
        throws IOException
    {
        final ModuleKey moduleKey = ModuleKey.from( params.getName(), params.getVersion() );
        final Module.Builder moduleBuilder = Module.newModule().
            moduleKey( moduleKey ).
            displayName( params.getDisplayName() ).
            info( params.getInfo() ).
            url( params.getUrl() ).
            vendorName( params.getVendorName() ).
            vendorUrl( params.getVendorUrl() ).
            config( params.getConfig() ).
            minSystemVersion( params.getMinSystemVersion() ).
            maxSystemVersion( params.getMaxSystemVersion() ).
            addContentTypeDependencies( params.getContentTypeDependencies() ).
            addModuleDependencies( params.getModuleDependencies() );

        final ModuleFileEntry moduleDirectoryEntry = params.getModuleDirectoryEntry();
        if ( moduleDirectoryEntry != null && moduleDirectoryEntry.isDirectory() && !moduleDirectoryEntry.isEmpty() )
        {
            final ModuleFileEntry.Builder moduleDirectoryBaseEntry = moduleBuilder.getModuleDirectoryEntry();
            for ( ModuleFileEntry fileEntry : moduleDirectoryEntry )
            {
                moduleDirectoryBaseEntry.addEntry( fileEntry );
            }
        }
        final Module module = moduleBuilder.build();

        final Path moduleDirPath = systemConfig.getModulesDir();
        Files.createDirectories( moduleDirPath );

        moduleExporter.exportToDirectory( module, moduleDirPath );
        return module;
    }

    public CreateModuleCommand params( final CreateModuleParams params )
    {
        this.params = params;
        return this;
    }

    public CreateModuleCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }

    public CreateModuleCommand moduleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
        return this;
    }
}
