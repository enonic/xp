package com.enonic.wem.api.resource;


import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;

public final class Resource
{
    private final ResourceKey key;

    private final ByteSource byteSource;

    private final long timestamp;

    private Resource( final Builder builder )
    {
        this.key = builder.key;
        this.byteSource = builder.byteSource;
        this.timestamp = builder.timestamp;
    }

    public ResourceKey getKey()
    {
        return this.key;
    }

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

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public ByteSource getByteSource()
    {
        return this.byteSource;
    }

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

    public static Builder newResource()
    {
        return new Builder();
    }

    public static Builder copyOf( final Resource resource )
    {
        return new Builder( resource );
    }

    public static class Builder
    {
        private ResourceKey key;

        private ByteSource byteSource;

        private long timestamp = 0;

        private Builder()
        {
        }

        private Builder( final Resource resource )
        {
            this.key = resource.key;
            this.byteSource = resource.byteSource;
            this.timestamp = resource.timestamp;
        }

        public Builder key( final ResourceKey key )
        {
            this.key = key;
            return this;
        }

        public Builder byteSource( final ByteSource byteSource )
        {
            this.byteSource = byteSource;
            return this;
        }

        public Builder stringValue( final String value )
        {
            this.byteSource = ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) );
            return this;
        }

        public Builder timestamp( final long timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Resource build()
        {
            return new Resource( this );
        }
    }
}
