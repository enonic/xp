package com.enonic.xp.resource;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Assert;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;

public class MockResourceService
    implements ResourceService
{
    private final File rootDir;

    public MockResourceService( final ClassLoader loader, final String path )
    {
        final URL url = loader.getResource( path );
        Assert.assertNotNull( "Could not find resource dir [" + path + "]", url );

        this.rootDir = new File( url.getFile() );
    }

    @Override
    public Resource getResource( final ResourceKey key )
    {
        final File file = toFile( key.getApplicationKey(), key.getPath() );
        return new FileResource( key, file );
    }

    @Override
    public ResourceKeys findFiles( final ApplicationKey key, final String path, final String ext, final boolean recursive )
    {
        final File dir = toFile( key, path );
        final List<File> files = findFiles( dir, recursive, ( f ) -> f.isFile() && f.getName().endsWith( "." + ext ) );
        return ResourceKeys.from( toKeys( key, files ) );
    }

    @Override
    public ResourceKeys findFolders( final ApplicationKey key, final String path )
    {
        final File dir = toFile( key, path );
        final List<File> files = findFiles( dir, false, File::isDirectory );
        return ResourceKeys.from( toKeys( key, files ) );
    }

    private File toAppDir( final ApplicationKey key )
    {
        return new File( this.rootDir, key.toString() );
    }

    private File toFile( final ApplicationKey key, final String path )
    {
        final File appDir = toAppDir( key );
        return new File( appDir, path.substring( 1 ) );
    }

    private ResourceKeys toKeys( final ApplicationKey appKey, final List<File> files )
    {
        final String appDir = toAppDir( appKey ).getAbsolutePath();
        final Iterator<ResourceKey> keys =
            files.stream().map( ( f ) -> ResourceKey.from( appKey, f.getAbsolutePath().substring( appDir.length() ) ) ).iterator();
        return ResourceKeys.from( keys );
    }

    private List<File> findFiles( final File dir, final boolean recursive, final Predicate<File> filter )
    {
        final List<File> files = Lists.newArrayList();
        findFiles( files, dir, recursive, filter );
        return files;
    }

    private void findFiles( final List<File> files, final File dir, final boolean recursive, final Predicate<File> filter )
    {
        final File[] children = dir.listFiles();
        if ( children == null )
        {
            return;
        }

        for ( final File child : children )
        {
            if ( filter.test( child ) )
            {
                files.add( child );
            }

            if ( recursive )
            {
                findFiles( files, child, true, filter );
            }
        }
    }
}
