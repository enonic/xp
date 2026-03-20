package com.enonic.xp.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Objects;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.security.MessageDigests;

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

    public static BlobKey sha256( final ByteSource in )
    {
        try
        {
            final MessageDigest digest = MessageDigests.sha256();
            try (InputStream is = in.openStream(); DigestInputStream dis = new DigestInputStream( is, digest ))
            {
                dis.transferTo( OutputStream.nullOutputStream() );
            }
            return from( "sha256:" + MessageDigests.formatHex( digest ) );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to create blobKey", e );
        }
    }
}
