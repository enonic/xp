package com.enonic.xp.core.impl.app.resolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;

public final class ClassLoaderApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ClassLoader loader;

    @Override
    public long filesHash( final String path )
    {
        return System.currentTimeMillis();
    }

    public ClassLoaderApplicationUrlResolver( final URLClassLoader loader )
    {
        this.loader = loader;
    }

    @Override
    public Set<String> findFiles()
    {
        final ClassPath cp;
        try
        {
            cp = ClassPath.from( this.loader );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return cp.getResources().stream().map( ClassPath.ResourceInfo::getResourceName ).collect( Collectors.toSet() );
    }

    @Override
    public URL findUrl( final String path )
    {
        final URL url = this.loader.getResource( normalizePath( path ) );

        if ( url == null )
        {
            return null;
        }
        else if ( "file".equalsIgnoreCase( url.getProtocol() ) )
        {
            try
            {
                if ( Files.isDirectory( Path.of( url.toURI() ) ) )
                {
                    return null;
                }
            }
            catch ( URISyntaxException e )
            {
                return null;
            }
            return url;
        }
        else if ( url.getPath().endsWith( "/" ) )
        {
            return null;
        }
        else
        {
            return url;
        }
    }

    private static String normalizePath( final String path )
    {
        return path.startsWith( "/" ) ? path.substring( 1 ) : path;
    }
}
