package com.enonic.xp.core.impl.resource;

import java.io.File;
import java.util.List;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.FileResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;

final class DevResourceLoader
    implements ResourceLoader
{
    private final BundleResourceLoader delegate;

    public DevResourceLoader()
    {
        this.delegate = new BundleResourceLoader();
    }

    @Override
    public Resource getResource( final Application app, ResourceKey key )
    {
        final List<String> paths = app.getSourcePaths();
        final File file = loadFromPaths( paths, key );

        if ( file != null )
        {
            return new FileResource( key, file );
        }

        return this.delegate.getResource( app, key );
    }

    public ResourceKeys findFolders( final Application app, final String path )
    {
        return this.delegate.findFolders( app, path );
    }

    private File loadFromPaths( final List<String> paths, final ResourceKey key )
    {
        for ( final String path : paths )
        {
            final File file = loadFromPath( path, key );
            if ( file != null )
            {
                return file;
            }
        }

        return null;
    }

    private File loadFromPath( final String path, final ResourceKey key )
    {
        final File file = new File( new File( path ), key.getPath().substring( 1 ) );
        if ( file.isFile() && file.exists() )
        {
            return file;
        }

        return null;
    }
}
