package com.enonic.wem.core.resource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;
import com.enonic.wem.core.config.SystemConfig;

final class ModuleResourceResolver
    implements ResourceResolver
{
    private SystemConfig systemConfig;

    @Override
    public Resource resolve( final ResourceKey key )
    {
        final File path = findPath( key );
        if ( !path.isFile() )
        {
            return null;
        }

        final Resource.Builder builder = Resource.newResource();
        builder.byteSource( Files.asByteSource( path ) );
        builder.timestamp( path.lastModified() );
        builder.key( key );
        return builder.build();
    }

    @Override
    public ResourceKeys getChildren( final ResourceKey parentKey )
    {
        final List<ResourceKey> keys = Lists.newArrayList();
        findChildren( keys, parentKey );
        return ResourceKeys.from( keys );
    }

    private void findChildren( final List<ResourceKey> keys, final ResourceKey parentKey )
    {
        final File file = findPath( parentKey );
        if ( file.isFile() )
        {
            keys.add( parentKey );
        }

        final File[] children = file.listFiles();
        if ( children == null )
        {
            return;
        }

        for ( final File child : children )
        {
            findChildren( keys, parentKey.resolve( child.getName() ) );
        }
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
