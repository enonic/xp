package com.enonic.xp.core.internal.security;

import java.nio.charset.StandardCharsets;
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
