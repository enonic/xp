package com.enonic.xp.core.internal.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class MessageDigests
{
    public static void updateWithIntLE( final MessageDigest digest, final int value )
    {
        digest.update( (byte) value );
        digest.update( (byte) ( value >>> 8 ) );
        digest.update( (byte) ( value >>> 16 ) );
        digest.update( (byte) ( value >>> 24 ) );
    }

    public static void updateWithDoubleLE( final MessageDigest digest, final double value )
    {
        final long bits = Double.doubleToRawLongBits( value );
        digest.update( (byte) bits );
        digest.update( (byte) ( bits >>> 8 ) );
        digest.update( (byte) ( bits >>> 16 ) );
        digest.update( (byte) ( bits >>> 24 ) );
        digest.update( (byte) ( bits >>> 32 ) );
        digest.update( (byte) ( bits >>> 40 ) );
        digest.update( (byte) ( bits >>> 48 ) );
        digest.update( (byte) ( bits >>> 56 ) );
    }

    public static void updateWithString( final MessageDigest digest, final String string )
    {
        final byte[] bytes = string.getBytes( StandardCharsets.UTF_8 );
        MessageDigests.updateWithIntLE( digest, bytes.length );
        digest.update( bytes );
    }

    public static MessageDigest digest( final MessageDigest digest, final IOSupplier<InputStream> inputStreamOpener )
        throws IOException
    {
        try (InputStream is = inputStreamOpener.get(); DigestInputStream dis = new DigestInputStream( is, digest ))
        {
            dis.transferTo( OutputStream.nullOutputStream() );
            return digest;
        }
    }

    public static String formatHex( final MessageDigest digest )
    {
        return HexFormat.of().formatHex( digest.digest() );
    }

    public static MessageDigest sha512()
    {
        return getInstance( "SHA-512" );
    }

    public static MessageDigest sha256()
    {
        return getInstance( "SHA-256" );
    }

    @FunctionalInterface
    public interface IOSupplier<T>
    {
        T get()
            throws IOException;
    }

    private static MessageDigest getInstance( final String algorithmName )
    {
        try
        {
            return MessageDigest.getInstance( algorithmName );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new AssertionError( e );
        }
    }
}
