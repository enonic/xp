package com.enonic.wem.core.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceNotFoundException;

public final class ResourceImpl
    implements Resource
{
    private final ResourceKey key;

    private final URL url;

    private final ByteSource source;

    public ResourceImpl( final ResourceKey key, final URL url )
    {
        this.key = key;
        this.url = url;
        this.source = this.url != null ? Resources.asByteSource( this.url ) : null;
    }

    @Override
    public ResourceKey getKey()
    {
        return this.key;
    }

    @Override
    public URL getUrl()
    {
        return this.url;
    }

    @Override
    public boolean exists()
    {
        try (final InputStream in = openStream())
        {
            return in != null;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    @Override
    public long getSize()
    {
        try
        {
            return openConnection().getContentLength();
        }
        catch ( final Exception e )
        {
            return -1;
        }
    }

    @Override
    public long getTimestamp()
    {
        try
        {
            return openConnection().getLastModified();
        }
        catch ( final Exception e )
        {
            return -1;
        }
    }

    private URLConnection openConnection()
    {
        openStream();

        try
        {
            return this.url.openConnection();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    private RuntimeException handleError( final IOException e )
    {
        if ( e instanceof FileNotFoundException )
        {
            throw new ResourceNotFoundException( this.key );
        }

        throw Throwables.propagate( e );
    }

    private ByteSource getByteSource()
    {
        if ( this.source == null )
        {
            throw new ResourceNotFoundException( this.key );
        }

        return this.source;
    }

    private CharSource getCharSource()
    {
        return getByteSource().asCharSource( Charsets.UTF_8 );
    }

    @Override
    public InputStream openStream()
    {
        try
        {
            return getByteSource().openStream();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    @Override
    public String readString()
    {
        try
        {
            return getCharSource().read();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    @Override
    public byte[] readBytes()
    {
        try
        {
            return getByteSource().read();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    @Override
    public List<String> readLines()
    {
        try
        {
            return getCharSource().readLines();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }
}
