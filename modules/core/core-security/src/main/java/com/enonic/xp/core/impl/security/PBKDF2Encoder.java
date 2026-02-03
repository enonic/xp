package com.enonic.xp.core.impl.security;

import java.security.MessageDigest;
import java.util.HexFormat;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.jspecify.annotations.NullMarked;

@NullMarked
final class PBKDF2Encoder
    implements PasswordVerifier
{
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final int LENGTH = 24;

    private static final int ITERATIONS = 1000;

    @Override
    public boolean verify( final char[] plainPassword, final String encodedPassword )
    {
        final String[] elements = encodedPassword.split( ":" );

        if ( elements.length != 3 )
        {
            throw new IllegalArgumentException( "Could not parse authentication key; wrong format" );
        }

        final String type = elements[0];
        if ( !"PBKDF2".equals( type ) )
        {
            throw new IllegalArgumentException( "Incorrect type of encryption, expected 'PBKDF2', got '" + type + "'" );
        }

        final byte[] salt = HexFormat.of().parseHex( elements[1] );
        final byte[] correctHash = HexFormat.of().parseHex( elements[2] );
        final byte[] generatedHash = hashPassword( plainPassword, salt );

        return MessageDigest.isEqual( correctHash, generatedHash );
    }

    private byte[] hashPassword( final char[] password, byte[] salt )
    {
        try
        {
            PBEKeySpec spec = new PBEKeySpec( password, salt, ITERATIONS, LENGTH * 8 );
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( ALGORITHM );
            return keyFactory.generateSecret( spec ).getEncoded();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
