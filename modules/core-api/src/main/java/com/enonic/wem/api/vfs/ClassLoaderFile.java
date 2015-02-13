package com.enonic.wem.api.vfs;

import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

final class ClassLoaderFile
    implements VirtualFile
{
    private final ClassLoader loader;

    private final String path;

    private final VirtualFilePath virtualFilePath;

    private URL url;

    private boolean folder;

    public ClassLoaderFile( final ClassLoader loader, final String path )
    {
        this.virtualFilePath = VirtualFilePath.from( path );
        this.loader = loader;
        this.path = cleanPath( path );
        this.url = this.loader.getResource( path.substring( 1 ) );
        this.folder = false;

        if ( this.url == null )
        {
            this.url = this.loader.getResource( path.substring( 1 ) + "/" );
            this.folder = this.url != null;
        }
    }

    public ClassLoaderFile( final ClassLoader loader, final String path, final boolean folder )
    {
        this( loader, path );
        this.folder = true;
    }

    private static String cleanPath( final String path )
    {
        final Iterable<String> parts = Splitter.on( '/' ).omitEmptyStrings().trimResults().split( path );
        return "/" + Joiner.on( '/' ).join( parts );
    }

    @Override
    public String getName()
    {
        if ( this.path.equals( "/" ) )
        {
            return "";
        }
        else
        {
            return this.path.substring( this.path.lastIndexOf( '/' ) );
        }
    }

    @Override
    public VirtualFilePath getPath()
    {
        return this.virtualFilePath;
    }

    @Override
    public URL getUrl()
    {
        return this.url;
    }

    @Override
    public boolean isFolder()
    {
        return this.folder;
    }

    @Override
    public boolean isFile()
    {
        return exists() && !isFolder();
    }

    @Override
    public List<VirtualFile> getChildren()
    {
        if ( !isFolder() )
        {
            return Lists.newArrayList();
        }

        return null;
    }

    @Override
    public CharSource getCharSource()
    {
        if ( !isFile() )
        {
            return null;
        }

        return Resources.asCharSource( this.url, Charsets.UTF_8 );
    }

    @Override
    public ByteSource getByteSource()
    {
        if ( !isFile() )
        {
            return null;
        }

        return Resources.asByteSource( this.url );
    }

    @Override
    public boolean exists()
    {
        if ( this.url == null )
        {
            return false;
        }

        try
        {
            this.url.openConnection().connect();
            return true;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    @Override
    public VirtualFile resolve( final VirtualFilePath path )
    {
        return new ClassLoaderFile( this.loader, this.path + "/" + path.getPath() );
    }
}
