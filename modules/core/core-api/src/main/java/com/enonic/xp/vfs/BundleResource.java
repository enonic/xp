package com.enonic.xp.vfs;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

class BundleResource
    implements VirtualFile
{
    private final static String PATTERN = "*";

    private final Bundle bundle;

    private final String path;

    private final VirtualFilePath virtualFilePath;

    public BundleResource( final Bundle bundle, final String path )
    {
        this.virtualFilePath = VirtualFilePaths.from( path, "/" );
        this.bundle = bundle;
        this.path = path;
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
            return this.path.substring( this.path.lastIndexOf( '/' ) + 1 );
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
        return doGetUrl();
    }

    private URL doGetUrl()
    {
        return this.bundle.getEntry( path );
    }

    @Override
    public boolean isFolder()
    {
        return this.path.endsWith( "/" );
    }

    @Override
    public boolean isFile()
    {
        return !this.path.endsWith( "/" );
    }

    @Override
    public List<VirtualFile> getChildren()
    {
        final List<VirtualFile> files = new ArrayList<>();
        final Iterator<URL> entries = bundle.findEntries( path, PATTERN, false ).asIterator();
        while ( entries.hasNext() )
        {
            files.add( new BundleResource( this.bundle, entries.next().getPath() ) );
        }
        return files;
    }

    @Override
    public CharSource getCharSource()
    {
        final URL resource = this.bundle.getResource( this.path );

        return Resources.asCharSource( resource, StandardCharsets.UTF_8 );
    }

    @Override
    public ByteSource getByteSource()
    {
        final URL resource = this.bundle.getResource( this.path );

        return Resources.asByteSource( resource );
    }

    @Override
    public boolean exists()
    {
        return this.bundle.getResource( path ) != null;
    }

    @Override
    public VirtualFile resolve( final VirtualFilePath path )
    {
        final URL entryUrl = this.bundle.getEntry( path.getPath() );

        if ( entryUrl == null )
        {
            return new NullResource( path );
        }

        return new BundleResource( this.bundle, entryUrl.getPath() );
    }


}
