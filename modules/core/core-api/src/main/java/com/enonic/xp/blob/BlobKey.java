package com.enonic.xp.blob;

import java.io.IOException;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

import com.enonic.xp.util.HexEncoder;

public final class BlobKey
{
    private final String key;

    private BlobKey( final String key )
    {
        this.key = key;
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

    private static BlobKey from( final byte... key )
    {
        return from( HexEncoder.toHex( key ) );
    }

    public static BlobKey from( final HashCode key )
    {
        return from( key.asBytes() );
    }

    public static BlobKey from( final ByteSource in )
    {
        try
        {
            return from( in.hash( Hashing.sha1() ) );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to create blobKey", e );
        }
    }
}
