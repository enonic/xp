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
public final class Resource
{
    private final ResourceKey key;

    private final URL url;

    private Resource( final ResourceKey key, final URL url )
    {
        this.key = key;
        this.url = url;
    }

    public ResourceKey getKey()
    {
        return this.key;
    }

    public URL getUrl()
    {
        return this.url;
    }

    public void requireExists()
    {
        if ( !exists() )
        {
            throw new ResourceNotFoundException( this.key );
        }
    }

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

    public Readable openReader()
    {
        return new InputStreamReader( openStream() );
    }

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

    public static Resource from( final ResourceKey key )
    {
        final URL url = ResourceUrlResolver.resolve( key );
        return new Resource( key, url );
    }
}
