package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.util.Exceptions;

final class GetAllModulesCommand
{
    private SystemConfig systemConfig;

    private ModuleExporter moduleExporter;

    public Modules execute()
    {
        try
        {
            return doExecute();
        }
        catch ( final IOException e )
        {
            throw Exceptions.newRutime( "Error occured finding modules." ).withCause( e );
        }
    }

    private Modules doExecute()
        throws IOException
    {
        final List<Module> modules = new ArrayList<>();
        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( this.systemConfig.getModulesDir() ))
        {
            for ( final Path moduleDir : ds )
            {
                if ( Files.isDirectory( moduleDir ) )
                {
                    final ModuleBuilder builder = this.moduleExporter.importFromDirectory( moduleDir );
                    if ( builder != null )
                    {
                        modules.add( builder.build() );
                    }
                }
            }
        }

        return Modules.from( modules );
    }

    public GetAllModulesCommand systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }

    public GetAllModulesCommand moduleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
        return this;
    }
}
