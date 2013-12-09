package com.enonic.wem.core.module;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.enonic.wem.api.command.module.DeleteModule;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.exporters.ModuleExporter;

public class DeleteModuleHandler
    extends CommandHandler<DeleteModule>
{
    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    @Override
    public void handle()
        throws Exception
    {
        final Path moduleDir = moduleResourcePathResolver.resolveModulePath( command.getModule() );
        if ( Files.notExists( moduleDir ) )
        {
            throw new ModuleNotFoundException( command.getModule() );
        }

        final Module module = moduleExporter.importFromDirectory( moduleDir ).build();
        FileUtils.deleteDirectory( moduleDir.toFile() );

        command.setResult( module );
    }

    @Inject
    public void setModuleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
    }

    @Inject
    public void setModuleImporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
    }
}
