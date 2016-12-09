package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

final class ResourceResolver
{
    private final static String SITE_PREFIX = "/site";

    private final ResourceService resourceService;

    private final ResourceKey baseKey;

    ResourceResolver( final ResourceService resourceService, final ResourceKey baseKey )
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

    private boolean exists( final ResourceKey key )
    {
        return this.resourceService.getResource( key ).exists();
    }
}
