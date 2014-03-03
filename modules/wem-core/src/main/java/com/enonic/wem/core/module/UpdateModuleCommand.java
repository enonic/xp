package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.UpdateModuleParams;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.util.Exceptions;

final class UpdateModuleCommand
{
    private UpdateModuleParams params;

    private SystemConfig systemConfig;

    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    public boolean execute()
    {
        this.params.validate();

        try
        {
            return doExecute();
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRutime( "Error updating module" ).withCause( e );
        }
    }

    private boolean doExecute()
        throws IOException
    {
        final Path modulesDir = systemConfig.getModulesDir();
        final Path moduleDir = moduleResourcePathResolver.resolveModulePath( params.getModuleKey() );
        if ( Files.notExists( moduleDir ) )
        {
            throw new ModuleNotFoundException( params.getModuleKey() );
        }

        Module module = moduleExporter.importFromDirectory( moduleDir ).build();
        Module editedModule = params.getEditor().edit( module );
        boolean edited = editedModule != null && !editedModule.equals( module );
        if ( edited )
        {
            moduleExporter.exportToDirectory( editedModule, modulesDir );
        }

        return edited;
    }

    public UpdateModuleCommand params( final UpdateModuleParams params )
    {
        this.params = params;
        return this;
    }

    public UpdateModuleCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }

    public UpdateModuleCommand moduleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
        return this;
    }

    public UpdateModuleCommand moduleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
        return this;
    }
}
