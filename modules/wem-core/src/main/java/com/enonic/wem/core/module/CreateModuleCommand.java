package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.CreateModuleSpec;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.util.Exceptions;

final class CreateModuleCommand
{
    private CreateModuleSpec spec;

    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter;

    public Module execute()
    {
        this.spec.validate();
        
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
        final ModuleKey moduleKey = ModuleKey.from( spec.getName(), spec.getVersion() );
        final Module.Builder moduleBuilder = Module.newModule().
            moduleKey( moduleKey ).
            displayName( spec.getDisplayName() ).
            info( spec.getInfo() ).
            url( spec.getUrl() ).
            vendorName( spec.getVendorName() ).
            vendorUrl( spec.getVendorUrl() ).
            config( spec.getConfig() ).
            minSystemVersion( spec.getMinSystemVersion() ).
            maxSystemVersion( spec.getMaxSystemVersion() ).
            addContentTypeDependencies( spec.getContentTypeDependencies() ).
            addModuleDependencies( spec.getModuleDependencies() );

        final ModuleFileEntry moduleDirectoryEntry = spec.getModuleDirectoryEntry();
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

    public CreateModuleCommand spec( final CreateModuleSpec spec )
    {
        this.spec = spec;
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
