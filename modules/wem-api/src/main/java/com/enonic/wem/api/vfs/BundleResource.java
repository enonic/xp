package com.enonic.wem.api.vfs;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;

import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.wem.api.util.Exceptions;

class BundleResource
    implements VirtualFile
{
    private final static String PATTERN = "*";

    private final Bundle bundle;

    private final String path;

    public BundleResource( final Bundle bundle, final String path )
    {
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
            return this.path.substring( this.path.lastIndexOf( '/' ) );
        }
    }

    @Override
    public String getPath()
    {
        return this.path;
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
        final Enumeration<URL> entries = bundle.findEntries( path, PATTERN, false );

        List<VirtualFile> files = Lists.newArrayList();

        while ( entries.hasMoreElements() )
        {
            files.add( new BundleResource( this.bundle, entries.nextElement().getPath() ) );
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
    public VirtualFile resolve( final String path )
    {
        final Path absolutePath = Paths.get( this.path, path );

        return new BundleResource( this.bundle, this.bundle.getEntry( absolutePath.toString() ).getPath() );
    }

    private URI getAsUri()
    {
        try
        {
            return this.bundle.getEntry( path ).toURI();
        }
        catch ( URISyntaxException e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
