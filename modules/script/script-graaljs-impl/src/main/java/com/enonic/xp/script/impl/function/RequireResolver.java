package com.enonic.xp.script.impl.function;

import java.util.List;

import com.google.common.io.Files;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

final class RequireResolver
{
    private final ResourceService resourceService;

    private final ResourceKey baseKey;

    RequireResolver( final ResourceService resourceService, final ResourceKey baseKey )
    {
        this.baseKey = baseKey;
        this.resourceService = resourceService;
    }

    public ResourceKey resolve( final String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return resolveAbsolute( path );
        }
        else
        {
            return resolveRelative( path );
        }
    }

    private ResourceKey resolveAbsolute( final String path )
    {
        return doResolve( this.baseKey.resolve( path ) );
    }

    private ResourceKey resolveRelative( final String path )
    {
        return doResolve( this.baseKey.resolve( "../" + path ) );
    }

    private ResourceKey doResolve( final ResourceKey key )
    {
        for ( final String path : findSearchPaths( key.getPath() ) )
        {
            final ResourceKey pathKey = ResourceKey.from( key.getApplicationKey(), path );
            if ( exists( pathKey ) )
            {
                return pathKey;
            }
        }

        return key;
    }

    static List<String> findSearchPaths( final String path )
    {
        if ( !Files.getFileExtension( path ).isEmpty() )
        {
            return List.of( path );
        }

        return List.of( path + ".js", path + "/index.js", path + ".json", path + "/index.json" );
    }

    private boolean exists( final ResourceKey key )
    {
        return this.resourceService.getResource( key ).exists();
    }
}
