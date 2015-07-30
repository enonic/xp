package com.enonic.xp.resource;

import java.io.File;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import com.google.common.base.Throwables;

final class MockUrlStreamHandlerFactory
    implements URLStreamHandlerFactory, ResourceUrlRegistry
{
    private File modulesDir;

    private ClassLoader modulesClassLoader;

    @Override
    public URL getUrl( final ResourceKey resourceKey )
    {
        URL url = resolveUrl( resourceKey.getApplicationKey().toString() + resourceKey.getPath() );
        if ( url != null )
        {
            return url;
        }

        url = resolveUrl( "modules/" + resourceKey.getApplicationKey().toString() + resourceKey.getPath() );
        if ( url != null )
        {
            return url;
        }

        return resolveUrl( resourceKey.getPath() );
    }

    private URL resolveUrl( final String path )
    {
        if ( this.modulesClassLoader != null )
        {
            final URL url = resolveUrl( this.modulesClassLoader, path );
            if ( url != null )
            {
                return url;
            }
        }

        if ( this.modulesDir != null )
        {
            final URL url = resolveUrl( this.modulesDir, path );
            if ( url != null )
            {
                return url;
            }
        }

        return null;
    }

    private static URL resolveUrl( final ClassLoader loader, final String path )
    {
        if ( loader == null )
        {
            return null;
        }

        if ( path.startsWith( "/" ) )
        {
            return loader.getResource( path.substring( 1 ) );
        }
        else
        {
            return loader.getResource( path );
        }
    }

    private static URL resolveUrl( final File dir, final String path )
    {
        if ( dir == null )
        {
            return null;
        }

        final File file = new File( dir, path );
        if ( !file.exists() )
        {
            return null;
        }

        try
        {
            return file.toURI().toURL();
        }
        catch ( final Exception e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public ResourceUrlRegistry applicationsDir( final File modulesDir )
    {
        this.modulesDir = modulesDir;
        return this;
    }

    @Override
    public ResourceUrlRegistry modulesClassLoader( final ClassLoader modulesClassLoader )
    {
        this.modulesClassLoader = modulesClassLoader;
        return this;
    }

    @Override
    public URLStreamHandler createURLStreamHandler( final String protocol )
    {
        if ( "module".equals( protocol ) )
        {
            return new MockUrlStreamHandler( this );
        }

        return null;
    }
}
