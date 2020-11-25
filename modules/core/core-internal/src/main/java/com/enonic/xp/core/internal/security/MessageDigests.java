package com.enonic.xp.core.internal.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigests
{
    public static MessageDigest sha512()
    {
        try
        {
            return MessageDigest.getInstance( "SHA-512" );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new AssertionError( e );
        }
    }
}
