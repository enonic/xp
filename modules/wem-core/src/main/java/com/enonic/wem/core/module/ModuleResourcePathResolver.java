package com.enonic.wem.core.module;


import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.config.SystemConfig;

public final class ModuleResourcePathResolver
{
    private final SystemConfig systemConfig;

    @Inject
    public ModuleResourcePathResolver( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    public Path resolveModulePath( final ModuleKey moduleKey )
    {
        final Path basePath = systemConfig.getModulesDir().toPath();
        return basePath.resolve( moduleKey.toString() );
    }

    public Path resolveResourcePath( final ModuleResourceKey moduleResource )
    {
        final Path moduleDir = resolveModulePath( moduleResource.getModuleKey() );

        final ResourcePath resourcePath = moduleResource.getPath();
        Path fullPath = moduleDir;
        for ( String pathEl : resourcePath )
        {
            fullPath = fullPath.resolve( pathEl );
        }
        return fullPath;
    }
}
