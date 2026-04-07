package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;

public final class ResourceResolver
{
    private final ResourceKey baseKey;

    public ResourceResolver( final ResourceKey baseKey )
    {
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
        return this.baseKey.resolve( path );
    }

    private ResourceKey resolveRelative( final String path )
    {
        return this.baseKey.resolve( "../" + path );
    }
}
