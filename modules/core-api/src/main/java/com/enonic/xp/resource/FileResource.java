package com.enonic.xp.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

@Beta
public final class FileResource
    implements Resource
{
    private final ResourceKey key;

    private final File file;

    public FileResource( final ResourceKey key, final File file )
    {
        this.key = key;
        this.file = file;
    }

    @Override
    public ResourceKey getKey()
    {
        return this.key;
    }

    @Override
    public URL getUrl()
    {
        URL url = null;
        try
        {
            url = this.file.toURI().toURL();
        }
        catch ( MalformedURLException e )
        {
            //Nothing to do
        }
        return url;
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
        return this.file.exists();
    }

    @Override
    public long getSize()
    {
        return this.file.exists() ? this.file.length() : -1;
    }

    @Override
    public long getTimestamp()
    {
        return this.file.exists() ? this.file.lastModified() : -1;
    }

    @Override
    public InputStream openStream()
    {
        try
        {
            return new FileInputStream( this.file );
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    @Override
    public Readable openReader()
    {
        try
        {
            return new FileReader( this.file );
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

    private RuntimeException handleError( final IOException e )
    {
        if ( e instanceof FileNotFoundException )
        {
            throw new ResourceNotFoundException( this.key );
        }

        throw Throwables.propagate( e );
    }

}
