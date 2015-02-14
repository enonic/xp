package com.enonic.wem.repo.internal.blob;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.ByteStreams;

import com.enonic.xp.core.blob.BlobKey;
import com.enonic.xp.core.util.Exceptions;

public final class BlobKeyCreator
{
    private static final String DIGEST = "SHA-1";

    public static BlobKey createKey( final byte[] data )
        throws IOException
    {
        return createKey( new ByteArrayInputStream( data ) );
    }

    public static BlobKey createKey( final InputStream in )
        throws IOException
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
        throws IOException
    {
        final MessageDigest digest = createMessageDigest();

        try (DigestOutputStream digestOut = new DigestOutputStream( out, digest ))
        {
            ByteStreams.copy( in, digestOut );
        }
        finally
        {
            in.close();
        }

        return new BlobKey( digest.digest() );
    }

    private static MessageDigest createMessageDigest()
    {
        try
        {
            return MessageDigest.getInstance( DIGEST );
        }
        catch ( final NoSuchAlgorithmException e )
        {
            throw Exceptions.unchecked( e );
        }
    }
}
