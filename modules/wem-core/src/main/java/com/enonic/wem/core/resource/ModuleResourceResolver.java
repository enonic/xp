package com.enonic.wem.core.resource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import com.enonic.wem.api.resource.Resource2;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.core.config.SystemConfig;

final class ModuleResourceResolver
    implements ResourceResolver
{
    private SystemConfig systemConfig;

    @Override
    public Resource2 resolve( final ResourceKey key )
    {
        final File path = findPath( key );
        if ( !path.isFile() )
        {
            return null;
        }

        final Resource2.Builder builder = Resource2.newResource();
        builder.byteSource( Files.asByteSource( path ) );
        builder.timestamp( path.lastModified() );
        builder.key( key );
        return builder.build();
    }

    @Override
    public ResourceKeys getChildren( final ResourceKey parentKey )
    {
        final File path = findPath( parentKey );
        if ( !path.isDirectory() )
        {
            return ResourceKeys.empty();
        }

        final File[] children = path.listFiles();
        final List<ResourceKey> keys = Lists.newArrayList();

        if ( children != null )
        {
            for ( final File child : children )
            {
                keys.add( parentKey.resolve( child.getName() ) );
            }
        }

        return ResourceKeys.from( keys );
    }

    private File findPath( final ResourceKey key )
    {
        final Path modulePath = this.systemConfig.getModulesDir().resolve( key.getModule().toString() );
        return modulePath.resolve( key.getPath().substring( 1 ) ).toFile();
    }

    public ModuleResourceResolver systemConfig( final SystemConfig systemConfig )
    {
        this.systemConfig = systemConfig;
        return this;
    }
}
