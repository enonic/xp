package com.enonic.wem.core.module;


import java.nio.file.Path;

import javax.inject.Inject;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.config.SystemConfig;

public final class ModuleResourcePathResolverImpl
    implements ModuleResourcePathResolver
{
    private final SystemConfig systemConfig;

    @Inject
    public ModuleResourcePathResolverImpl( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Override
    public Path resolveModulePath( final ModuleKey moduleKey )
    {
        final Path basePath = systemConfig.getModulesDir();
        return basePath.resolve( moduleKey.toString() );
    }

    @Override
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
