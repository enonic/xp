package com.enonic.xp.blob;

import java.io.IOException;
import java.util.HexFormat;
import java.util.Objects;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public final class BlobKey
{
    private final String key;

    private BlobKey( final String key )
    {
        this.key = Objects.requireNonNull( key );
    }

    @Override
    public String toString()
    {
        return this.key;
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof BlobKey ) && this.key.equals( ( (BlobKey) o ).key );
    }

    @Override
    public int hashCode()
    {
        return this.key.hashCode();
    }

    public static BlobKey from( final String key )
    {
        return new BlobKey( key );
    }

    public static BlobKey from( final ByteSource in )
    {
        try
        {
            return from( HexFormat.of().formatHex( in.hash( Hashing.sha1() ).asBytes() ) );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to create blobKey", e );
        }
    }
}
