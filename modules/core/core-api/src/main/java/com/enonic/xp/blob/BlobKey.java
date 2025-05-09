package com.enonic.xp.blob;

import java.io.IOException;
import java.util.Objects;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

import com.enonic.xp.util.HexEncoder;

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
    public int hashCode()
    {
        return this.key.hashCode();
    }

    @Override
    public boolean equals( final Object object )
    {
        return ( object instanceof BlobKey ) && this.key.equals( ( (BlobKey) object ).key );
    }

    public static BlobKey from( final String key )
    {
        return new BlobKey( key );
    }

    public static BlobKey from( final ByteSource in )
    {
        try
        {
            return from( HexEncoder.toHex( in.hash( Hashing.sha1() ).asBytes() ) );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to create blobKey", e );
        }
    }
}
