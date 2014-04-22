package com.enonic.wem.core.resource;

import java.io.File;
import java.nio.file.Path;

import javax.inject.Inject;

import com.google.common.io.Files;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceReference;
import com.enonic.wem.core.config.SystemConfig;

public final class ResourceServiceImpl
    extends AbstractResourceService
{
    protected final SystemConfig systemConfig;

    @Inject
    public ResourceServiceImpl( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
    }

    @Override
    protected Resource resolve( final ModuleResourceKey key )
    {
        final File path = findPath( key );
        if ( !path.isFile() )
        {
            return null;
        }

        return new ResourceImpl( key ).
            byteSource( Files.asByteSource( path ) ).
            timestamp( path.lastModified() );
    }

    private File findPath( final ModuleResourceKey key )
    {
        final Path modulePath = this.systemConfig.getModulesDir().resolve( key.getModule().toString() );
        return modulePath.resolve( key.getPath().substring( 1 ) ).toFile();
    }

    @Override
    protected Resource resolve( final ResourceReference ref )
    {
        return null;
    }
}
