package com.enonic.wem.api.resource;


import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.io.ByteSource;

public final class Resource
{
    private final String name;

    private final ByteSource byteSource;

    private final String postfix;

    private final long size;

    private Resource( final Builder builder )
    {
        this.name = builder.name;
        this.byteSource = builder.byteSource;
        this.postfix = builder.postfix;
        this.size = builder.size;
    }

    public String getPostfix()
    {
        return postfix;
    }

    public long getSize()
    {
        return size;
    }

    public String getName()
    {
        return name;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
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

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "name", name ).
            add( "byteSource", byteSource ).
            add( "postfix", postfix ).
            add( "size", size < 0 ? "unknown" : size ).
            omitNullValues().
            toString();
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
        private String name;

        private ByteSource byteSource;

        private String postfix;

        private long size = -1;

        private Builder()
        {
        }

        private Builder( final Resource resource )
        {
            this.name = resource.name;
            this.byteSource = resource.byteSource;
            this.postfix = resource.postfix;
            this.size = resource.size;
        }

        public Builder name( final String name )
        {
            this.name = name;
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

        public Builder postfix( final String postfix )
        {
            this.postfix = postfix;
            return this;
        }

        public Builder size( final long size )
        {
            this.size = size;
            return this;
        }

        public Resource build()
        {
            return new Resource( this );
        }
    }

}