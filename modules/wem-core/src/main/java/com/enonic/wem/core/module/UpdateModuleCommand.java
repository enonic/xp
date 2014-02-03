package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.UpdateModuleSpec;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.util.Exceptions;

final class UpdateModuleCommand
{
    private UpdateModuleSpec spec;

    private SystemConfig systemConfig;

    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    public boolean execute()
    {
        this.spec.validate();

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
        final Path moduleDir = moduleResourcePathResolver.resolveModulePath( spec.getModuleKey() );
        if ( Files.notExists( moduleDir ) )
        {
            throw new ModuleNotFoundException( spec.getModuleKey() );
        }

        Module module = moduleExporter.importFromDirectory( moduleDir ).build();
        Module editedModule = spec.getEditor().edit( module );
        boolean edited = editedModule != null && !editedModule.equals( module );
        if ( edited )
        {
            moduleExporter.exportToDirectory( editedModule, modulesDir );
        }

        return edited;
    }

    public UpdateModuleCommand spec( final UpdateModuleSpec spec )
    {
        this.spec = spec;
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
