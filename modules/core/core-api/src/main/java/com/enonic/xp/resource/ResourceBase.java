package com.enonic.xp.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.CharSource;

public abstract class ResourceBase
    implements Resource
{
    private final ResourceKey key;

    public ResourceBase( final ResourceKey key )
    {
        this.key = key;
    }

    @Override
    public final ResourceKey getKey()
    {
        return this.key;
    }

    @Override
    public final InputStream openStream()
    {
        try
        {
            return getBytes().openStream();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    @Override
    public final Readable openReader()
    {
        try
        {
            return getCharSource().openStream();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    @Override
    public final void requireExists()
    {
        if ( !exists() )
        {
            throw new ResourceNotFoundException( this.key );
        }
    }

    @Override
    public final String readString()
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
    public final byte[] readBytes()
    {
        try
        {
            return getBytes().read();
        }
        catch ( final IOException e )
        {
            throw handleError( e );
        }
    }

    private CharSource getCharSource()
    {
        return getBytes().asCharSource( Charsets.UTF_8 );
    }

    @Override
    public final List<String> readLines()
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

    protected final RuntimeException handleError( final IOException e )
    {
        if ( e instanceof FileNotFoundException )
        {
            throw new ResourceNotFoundException( this.key );
        }

        throw Throwables.propagate( e );
    }
}
