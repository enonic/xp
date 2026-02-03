package com.enonic.xp.core.internal.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigests
{
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
