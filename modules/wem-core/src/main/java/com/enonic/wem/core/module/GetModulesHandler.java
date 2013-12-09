package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.enonic.wem.api.command.module.GetModules;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.ModuleExporter;

public class GetModulesHandler
    extends CommandHandler<GetModules>
{

    private SystemConfig systemConfig;

    private ModuleResourcePathResolver moduleResourcePathResolver;

    private ModuleExporter moduleExporter;

    @Override
    public void handle()
        throws Exception
    {
        final ModuleKeys moduleKeys = command.getModules();

        List<Module> modules;

        if ( moduleKeys != null )
        {
            modules = getModulesByKeys( moduleKeys );
        }
        else
        {
            modules = getAllModules();
        }

        command.setResult( Modules.from( modules ) );
    }

    private List<Module> getModulesByKeys( ModuleKeys moduleKeys )
        throws IOException
    {
        List<Module> modules = new ArrayList<>();
        for ( ModuleKey moduleKey : moduleKeys )
        {
            final Path moduleDir = moduleResourcePathResolver.resolveModulePath( moduleKey );
            if ( Files.isDirectory( moduleDir ) )
            {
                Module module = moduleExporter.importFromDirectory( moduleDir ).build();
                modules.add( module );
            }
        }
        return modules;
    }

    private List<Module> getAllModules()
        throws IOException
    {
        List<Module> modules = new ArrayList<>();
        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( systemConfig.getModulesDir() ))
        {
            for ( Path moduleDir : ds )
            {
                if ( Files.isDirectory( moduleDir ) )
                {
                    Module module = moduleExporter.importFromDirectory( moduleDir ).build();
                    modules.add( module );
                }
            }
        }
        return modules;
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Inject
    public void setModuleResourcePathResolver( final ModuleResourcePathResolver moduleResourcePathResolver )
    {
        this.moduleResourcePathResolver = moduleResourcePathResolver;
    }

    @Inject
    public void setModuleExporter( final ModuleExporter moduleExporter )
    {
        this.moduleExporter = moduleExporter;
    }
}
