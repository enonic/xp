package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.util.Exceptions;

final class GetModulesCommand
{
    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    private ModuleKeys keys;

    public Modules execute()
    {
        try
        {
            return doExecute();
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRutime( "Error occurred finding modules." ).withCause( e );
        }
    }

    private Modules doExecute()
        throws IOException
    {
        final List<Module> modules = new ArrayList<>();
        for ( final ModuleKey moduleKey : this.keys )
        {
            final Path moduleDir = this.moduleResourcePathResolver.resolveModulePath( moduleKey );
            if ( Files.isDirectory( moduleDir ) )
            {
                Module module = this.moduleExporter.importFromDirectory( moduleDir ).build();
                modules.add( module );
            }
        }

        return Modules.from( modules );
    }

    public GetModulesCommand keys( final ModuleKeys keys )
    {
        this.keys = keys;
        return this;
    }

    public GetModulesCommand moduleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
        return this;
    }

    public GetModulesCommand moduleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
        return this;
    }
}
