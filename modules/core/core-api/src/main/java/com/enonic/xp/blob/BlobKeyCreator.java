package com.enonic.xp.blob;

import java.io.IOException;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

public final class BlobKeyCreator
{
    public static BlobKey createKey( final ByteSource in )
    {
        try
        {
            return new BlobKey( in.hash( Hashing.sha1() ).asBytes() );
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to create blobKey", e );
        }
    }
}
