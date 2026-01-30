package com.enonic.xp.core.impl.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

final class PBKDF2Encoder
    implements PasswordEncoder
{
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final int LENGTH = 24;

    private static final int ITERATIONS = 1000;

    private final SecureRandom secureRandom;

    PBKDF2Encoder(final SecureRandom secureRandom)
    {
        this.secureRandom = secureRandom;
    }

    @Override
    public String encodePassword( final String plainPassword )
    {
        final byte[] salt = createSalt();

        final byte[] encodedPwd = encodePassword( plainPassword.toCharArray(), salt, LENGTH, ITERATIONS );

        final String saltAsString = HexFormat.of().formatHex( salt );
        final String encodedAsString = HexFormat.of().formatHex( encodedPwd );
        return new AuthenticationHash( saltAsString, encodedAsString, this.getType() ).toString();
    }

    @Override
    public boolean validate( final String key, final String correctPasswordKey )
    {
        if ( Objects.requireNonNullElse( key, "" ).isEmpty() || Objects.requireNonNullElse( correctPasswordKey, "" ).isEmpty() )
        {
            return false;
        }

        final AuthenticationHash authenticationHash = AuthenticationHash.from( correctPasswordKey );

        final String type = authenticationHash.type;
        if ( !type.equals( this.getType() ) )
        {
            throw new IllegalArgumentException( "Incorrect type of encryption, expected '" + this.getType() + "', got '" + type + "'" );
        }

        final byte[] correctHash = HexFormat.of().parseHex( authenticationHash.pwd );
        final byte[] generatedHash = encodePassword( key.toCharArray(), HexFormat.of().parseHex( authenticationHash.salt ), LENGTH, ITERATIONS );

        return MessageDigest.isEqual( correctHash, generatedHash );
    }

    @Override
    public String getType()
    {
        return "PBKDF2";
    }


    private byte[] encodePassword( final char[] password, byte[] salt, final int length, final int iterationCount )
    {
        try
        {
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterationCount, length * 8 );
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( ALGORITHM );
            return keyFactory.generateSecret( spec ).getEncoded();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private byte[] createSalt()
    {
        byte[] salt = new byte[20];
        secureRandom.nextBytes( salt );
        return salt;
    }

    private static class AuthenticationHash
    {
        private final String salt;

        private final String pwd;

        private final String type;

        private static final String SEPARATOR = ":";

        private AuthenticationHash( final String salt, final String pwd, final String type )
        {
            this.salt = salt;
            this.pwd = pwd;
            this.type = type;
        }

        public static AuthenticationHash from( final String key )
        {
            final String[] elements = key.split( SEPARATOR );

            if ( elements.length != 3 )
            {
                throw new IllegalArgumentException( "Could not parse authentication key; wrong format" );
            }

            final String name = elements[0];
            final String salt = elements[1];
            final String pwd = elements[2];

            return new AuthenticationHash( salt, pwd, name );
        }

        @Override
        public String toString()
        {
            return this.type + SEPARATOR + this.salt + SEPARATOR + this.pwd;
        }
    }
}
