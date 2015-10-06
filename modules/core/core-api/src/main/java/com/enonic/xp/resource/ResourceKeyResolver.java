package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

public final class ResourceKeyResolver
{
    private final String basePath;

    public ResourceKeyResolver( final String basePath )
    {
        this.basePath = basePath != null ? basePath : "";
    }

    public ResourceKey resolve( final ResourceKey parent, final String relPath )
    {
        return resolveFromDir( ResourceKey.from( parent.getApplicationKey(), parent.getPath() + "/.." ), relPath );
    }

    public ResourceKey resolveFromDir( final ResourceKey parent, final String relPath )
    {
        if ( relPath.startsWith( "/" ) )
        {
            return newKey( parent.getApplicationKey(), this.basePath, relPath );
        }

        final String path = parent.getPath() + "/" + relPath;
        if ( path.startsWith( this.basePath + "/" ) )
        {
            return newKey( parent.getApplicationKey(), "", path );
        }

        return newKey( parent.getApplicationKey(), this.basePath, path );
    }

    private ResourceKey newKey( final ApplicationKey appKey, final String prefix, final String path )
    {
        final ResourceKey key = ResourceKey.from( appKey, prefix + ResourceKey.from( appKey, path ).getPath() );
        if ( key.getPath().startsWith( this.basePath ) || key.getPath().equals( this.basePath ) )
        {
            return key;
        }

        return ResourceKey.from( appKey, this.basePath + key.getPath() );
    }
}
