package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.util.Exceptions;

final class GetModuleCommand
{
    private ModuleKey key;

    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    public Module execute()
    {
        final Path moduleDir = this.moduleResourcePathResolver.resolveModulePath( this.key );
        if ( Files.exists( moduleDir ) )
        {
            try
            {
                return this.moduleExporter.importFromDirectory( moduleDir ).build();
            }
            catch ( final IOException e )
            {
                throw Exceptions.newRutime( "Error fetching module [{0}]", this.key ).withCause( e );
            }
        }

        throw new ModuleNotFoundException( this.key );
    }

    public GetModuleCommand key( final ModuleKey key )
    {
        this.key = key;
        return this;
    }

    public GetModuleCommand moduleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
        return this;
    }

    public GetModuleCommand moduleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
        return this;
    }
}
