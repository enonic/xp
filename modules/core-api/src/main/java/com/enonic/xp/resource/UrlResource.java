package com.enonic.xp.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

@Beta
public final class UrlResource
    implements Resource
{
    private final ResourceKey key;

    private final URL url;

    public UrlResource( final ResourceKey key, final URL url )
    {
        this.key = key;
        this.url = url;
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
    public void requireExists()
    {
        if ( !exists() )
        {
            throw new ResourceNotFoundException( this.key );
        }
    }

    @Override
    public boolean exists()
    {
        try
        {
            openConnection();
            return true;
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
        if ( this.url == null )
        {
            throw new ResourceNotFoundException( this.key );
        }

        try
        {
            final URLConnection connection = this.url.openConnection();
            if ( connection == null )
            {
                throw new ResourceNotFoundException( this.key );
            }

            connection.connect();
            return connection;
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

    @Override
    public InputStream openStream()
    {
        try
        {
            return openConnection().getInputStream();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    @Override
    public Readable openReader()
    {
        return new InputStreamReader( openStream() );
    }

    @Override
    public String readString()
    {
        try
        {
            return CharStreams.toString( openReader() );
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
            try (final InputStream stream = openStream())
            {
                return ByteStreams.toByteArray( stream );
            }
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
            return CharStreams.readLines( openReader() );
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }
}
