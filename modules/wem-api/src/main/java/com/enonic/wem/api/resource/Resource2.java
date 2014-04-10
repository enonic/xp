package com.enonic.wem.api.resource;

import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;

public final class Resource2
{
    private final ResourceKey key;

    private final ByteSource byteSource;

    private final long timestamp;

    private Resource2( final Builder builder )
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

    public String getAsString()
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

    public static Builder newResource()
    {
        return new Builder();
    }

    public static Builder copyOf( final Resource2 resource )
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

        private Builder( final Resource2 resource )
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

        public Resource2 build()
        {
            return new Resource2( this );
        }
    }
}
