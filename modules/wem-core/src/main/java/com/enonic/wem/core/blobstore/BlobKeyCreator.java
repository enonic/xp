package com.enonic.wem.core.blobstore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.blob.BlobKey;

public final class BlobKeyCreator
{
    private static final String DIGEST = "SHA-1";

    public static BlobKey createKey( final byte[] data )
        throws BlobStoreException
    {
        return createKey( new ByteArrayInputStream( data ) );
    }

    public static BlobKey createKey( final InputStream in )
        throws BlobStoreException
    {
        return createKey( in, new OutputStream()
        {
            @Override
            public void write( final int b )
                throws IOException
            {
                // Do nothing
            }
        } );
    }

    public static BlobKey createKey( final InputStream in, final OutputStream out )
        throws BlobStoreException
    {
        try
        {
            final MessageDigest digest = createMessageDigest();
            final DigestOutputStream digestOut = new DigestOutputStream( out, digest );

            try
            {
                ByteStreams.copy( in, digestOut );
            }
            finally
            {
                digestOut.close();
                in.close();
            }

            return new BlobKey( digest.digest() );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to create blob key", e );
        }
    }

    private static MessageDigest createMessageDigest()
    {
        try
        {
            return MessageDigest.getInstance( DIGEST );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new BlobStoreException( DIGEST + " not available", e );
        }
    }
}
