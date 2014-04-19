package com.enonic.wem.core.resource;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

public final class ResourceImpl
    implements Resource
{
    private final ModuleResourceKey key;

    private ByteSource byteSource;

    private long timestamp;

    public ResourceImpl( final ModuleResourceKey key )
    {
        this.key = key;
    }

    @Override
    public ModuleResourceKey getKey()
    {
        return this.key;
    }

    @Override
    public long getSize()
    {
        try
        {
            return this.byteSource.size();
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public ByteSource getByteSource()
    {
        return this.byteSource;
    }

    @Override
    public String readAsString()
    {
        try
        {
            return this.byteSource.asCharSource( Charsets.UTF_8 ).read();
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public List<String> readLines()
    {
        try
        {
            return this.byteSource.asCharSource( Charsets.UTF_8 ).readLines();
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    public ResourceImpl timestamp( final long timestamp )
    {
        this.timestamp = timestamp;
        return this;
    }

    public ResourceImpl byteSource( final ByteSource byteSource )
    {
        this.byteSource = byteSource;
        return this;
    }

    public ResourceImpl stringValue( final String stringValue )
    {
        return byteSource( ByteSource.wrap( stringValue.getBytes( Charsets.UTF_8 ) ) );
    }
}
