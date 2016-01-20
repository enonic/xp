package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

final class ResourceResolver
{
    private final static String SITE_PREFIX = "/site";

    private final static String SCRIPT_SUFFIX = ".js";

    private final static String DEFAULT_RESOURCE = "/index.js";

    private final ResourceService resourceService;

    private final ResourceKey baseKey;

    public ResourceResolver( final ResourceService resourceService, final ResourceKey baseKey )
    {
        this.resourceService = resourceService;
        this.baseKey = baseKey;
    }

    public ResourceKey resolve( final String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return resolveAbsolute( path );
        }

        return resolveRelative( path );
    }

    private ResourceKey resolveAbsolute( final String path )
    {
        final ResourceKey key = this.baseKey.resolve( SITE_PREFIX + path );
        if ( exists( key ) )
        {
            return key;
        }

        return this.baseKey.resolve( path );
    }

    private ResourceKey resolveRelative( final String path )
    {
        return this.baseKey.resolve( "../" + path );
    }

    public ResourceKey resolveJs( final String path )
    {
        if ( !path.endsWith( SCRIPT_SUFFIX ) )
        {
            final ResourceKey resolved = resolveJs( path + SCRIPT_SUFFIX );
            if ( exists( resolved ) )
            {
                return resolved;
            }
            else
            {
                return resolveJs( path + DEFAULT_RESOURCE );
            }
        }

        if ( path.startsWith( "/" ) )
        {
            return resolveAbsolute( path );
        }

        if ( path.startsWith( "../" ) || path.startsWith( "./" ) )
        {
            return resolveRelative( path );
        }

        final ResourceKey key = resolveRelative( path );
        if ( exists( key ) )
        {
            return key;
        }

        return resolveLibScan( this.baseKey.resolve( ".." ), path );
    }

    private ResourceKey resolveLibScan( final ResourceKey parent, final String path )
    {
        if ( parent.isRoot() )
        {
            return parent.resolve( "/lib/" + path );
        }

        final ResourceKey key = parent.resolve( "lib/" + path );
        if ( exists( key ) )
        {
            return key;
        }

        return resolveLibScan( parent.resolve( ".." ), path );
    }

    private boolean exists( final ResourceKey key )
    {
        return this.resourceService.getResource( key ).exists();
    }
}
