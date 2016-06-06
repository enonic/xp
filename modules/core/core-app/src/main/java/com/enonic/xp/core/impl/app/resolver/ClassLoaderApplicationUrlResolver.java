package com.enonic.xp.core.impl.app.resolver;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;

import com.enonic.xp.util.Exceptions;

public final class ClassLoaderApplicationUrlResolver
    extends ApplicationUrlResolverBase
{
    private final ClassLoader loader;

    private final ClassPath cp;

    private Set<String> files;

    public ClassLoaderApplicationUrlResolver( final ClassLoader loader )
    {
        this.loader = loader;
        try
        {
            this.cp = ClassPath.from( this.loader );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public Set<String> findFiles()
    {
        if ( this.files == null )
        {
            this.files = doFindFiles();
        }

        return this.files;
    }

    private Set<String> doFindFiles()
    {
        return this.cp.getResources().stream().map( ClassPath.ResourceInfo::getResourceName ).collect( Collectors.toSet() );
    }

    @Override
    public URL findUrl( final String path )
    {
        final String normalized = normalizePath( path );
        return this.loader.getResource( normalized );
    }

    public static ClassLoaderApplicationUrlResolver create( final URL... urls )
    {
        return create( Lists.newArrayList( urls ) );
    }

    public static ClassLoaderApplicationUrlResolver create( final Iterable<URL> urls )
    {
        final URL[] array = Iterables.toArray( urls, URL.class );
        final URLClassLoader loader = new URLClassLoader( array, null );
        return new ClassLoaderApplicationUrlResolver( loader );
    }
}
