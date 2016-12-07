package com.enonic.xp.script.impl.function;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
        else if ( path.startsWith( "./" ) || path.startsWith( "../" ) )
        {
            return resolveRelative( path );
        }
        else
        {
            return resolveLibScan( path );
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

    private ResourceKey resolveLibScan( final String path )
    {
        final ResourceKey key = doResolve( this.baseKey.resolve( "/lib/" + path ) );
        if ( exists( key ) )
        {
            return key;
        }

        return resolveRelative( path );
    }

    private ResourceKey doResolve( final ResourceKey key )
    {
        for ( final String path : findAllSearchPaths( key.getPath() ) )
        {
            final ResourceKey pathKey = ResourceKey.from( key.getApplicationKey(), path );
            if ( exists( pathKey ) )
            {
                return pathKey;
            }
        }

        return key;
    }

    static List<String> findAllSearchPaths( final String path )
    {
        final List<String> paths = findSearchPaths( path );
        paths.addAll( paths.stream().map( it -> "/site" + it ).collect( Collectors.toList() ) );
        return paths;
    }

    private static List<String> findSearchPaths( final String path )
    {
        if ( !Strings.isNullOrEmpty( Files.getFileExtension( path ) ) )
        {
            return Lists.newArrayList( path );
        }

        return Lists.newArrayList( path + ".js", path + "/index.js", path + ".json", path + "/index.json" );
    }

    private boolean exists( final ResourceKey key )
    {
        return this.resourceService.getResource( key ).exists();
    }
}
