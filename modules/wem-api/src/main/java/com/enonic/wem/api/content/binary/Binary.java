package com.enonic.wem.api.content.binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

public final class Binary
{
    private final byte[] data;

    private Binary( final byte[] data )
    {
        this.data = data;
    }

    public InputStream asInputStream()
    {
        return new ByteArrayInputStream( this.data );
    }

    public byte[] toByteArray()
    {
        return Arrays.copyOf( data, data.length );
    }

    public int getSize()
    {
        return data.length;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof Binary ) )
        {
            return false;
        }

        final Binary that = (Binary) o;
        return Arrays.equals( this.data, that.data );
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode( data );
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "data", data ).
            add( "size", getSize() ).
            toString();
    }

    public static Binary from( final byte[] data )
    {
        Preconditions.checkNotNull( data, "data is mandatory" );
        Preconditions.checkArgument( data.length > 0, "data cannot be empty" );
        return new Binary( Arrays.copyOf( data, data.length ) );
    }

    public static Binary from( final InputStream inputStream )
    {
        Preconditions.checkNotNull( inputStream, "data is mandatory" );
        try
        {
            return new Binary( ByteStreams.toByteArray( inputStream ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to load binary from input stream", e );
        }
    }
}
