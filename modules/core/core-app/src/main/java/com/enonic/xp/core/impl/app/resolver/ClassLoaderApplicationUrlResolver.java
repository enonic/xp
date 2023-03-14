package com.enonic.xp.core.impl.app.resolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.UrlResource;

public final class ClassLoaderApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ClassLoader loader;

    private final ApplicationKey applicationKey;

    public ClassLoaderApplicationUrlResolver( final URLClassLoader loader, final ApplicationKey applicationKey )
    {
        this.loader = loader;
        this.applicationKey = applicationKey;
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
        return cp.getResources()
            .stream()
            .map( ClassPath.ResourceInfo::getResourceName )
            .collect( Collectors.toCollection( LinkedHashSet::new ) );
    }

    @Override
    public Resource findResource( final String path )
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
            return new UrlResource( ResourceKey.from( applicationKey, path ), url, "file" );
        }
        else if ( url.getPath().endsWith( "/" ) )
        {
            return null;
        }
        else
        {
            return new UrlResource( ResourceKey.from( applicationKey, path ), url, "file" );
        }
    }

    private static String normalizePath( final String path )
    {
        return path.startsWith( "/" ) ? path.substring( 1 ) : path;
    }
}
