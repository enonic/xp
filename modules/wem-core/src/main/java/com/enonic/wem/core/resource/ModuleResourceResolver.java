package com.enonic.wem.core.resource;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

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

        try
        {
            return new Resource2Impl( key, path.toURI().toURL() );
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
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
