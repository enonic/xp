package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.util.Exceptions;

final class DeleteModuleCommand
{
    private ModuleKey key;

    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    public Module execute()
    {
        final Path moduleDir = moduleResourcePathResolver.resolveModulePath( this.key );
        if ( Files.notExists( moduleDir ) )
        {
            throw new ModuleNotFoundException( this.key );
        }

        try
        {
            final ModuleBuilder moduleBuilder = this.moduleExporter.importFromDirectory( moduleDir );
            final Module module = moduleBuilder == null ? null : moduleBuilder.build();
            FileUtils.deleteDirectory( moduleDir.toFile() );
            return module;
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRutime( "Error deleting module [{0}]", this.key ).withCause( e );
        }
    }

    public DeleteModuleCommand key( final ModuleKey key )
    {
        this.key = key;
        return this;
    }

    public DeleteModuleCommand moduleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
        return this;
    }

    public DeleteModuleCommand moduleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
        return this;
    }
}
