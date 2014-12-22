package com.enonic.wem.core.security;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.google.common.base.Strings;

import com.enonic.wem.api.util.Exceptions;

public class PBKDF2Encoder
    implements PasswordEncoder
{
    private static final String SEPARATOR = ":";

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    private static final int LENGTH = 24;

    private static final int ITERATIONS = 1000;

    public PBKDF2Encoder()
    {
    }

    @Override
    public String encodePassword( final String plainPassword )
    {
        return doEncode( plainPassword );
    }

    private String doEncode( final String plainPassword )
    {
        final byte[] salt = createSalt();

        final byte[] encodedPwd = encodePassword( plainPassword.toCharArray(), salt, LENGTH, ITERATIONS );

        final String saltAsString = toHex( salt );
        final String encodedAsString = toHex( encodedPwd );
        return generateTypedString( encodedAsString, saltAsString );
    }

    @Override
    public boolean validate( final String key, final String correctPasswordKey )
    {
        final AuthenticationHash authenticationHash = AuthenticationHash.from( correctPasswordKey );

        if ( authenticationHash == null )
        {
            return false;
        }

        final String type = authenticationHash.type;
        if ( !type.equals( this.getType() ) )
        {
            throw new IllegalArgumentException( "Incorrect type of encryption, expected '" + this.getType() + "', got '" + type + "'" );
        }

        final byte[] correctHash = fromHex( authenticationHash.pwd );
        final byte[] generatedHash = encodePassword( key.toCharArray(), fromHex( authenticationHash.salt ), LENGTH, ITERATIONS );

        return Arrays.equals( correctHash, generatedHash );
    }

    @Override
    public String getType()
    {
        return "PBKDF2";
    }

    private String generateTypedString( final String encodedPwd, final String salt )
    {
        return this.getType() + SEPARATOR + salt + SEPARATOR + encodedPwd;
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
            throw Exceptions.unchecked( e );
        }
    }

    private byte[] createSalt()
    {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[20];
        random.nextBytes( salt );
        return salt;
    }

    private static String toHex( final byte[] value )
    {
        return new String( Hex.encodeHex( value ) );
    }

    private static byte[] fromHex( final String value )
    {
        try
        {
            return Hex.decodeHex( value.toCharArray() );
        }
        catch ( DecoderException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private static class AuthenticationHash
    {
        private final String salt;

        private final String pwd;

        private final String type;

        private AuthenticationHash( final String salt, final String pwd, final String type )
        {
            this.salt = salt;
            this.pwd = pwd;
            this.type = type;
        }

        public static AuthenticationHash from( final String key )
        {
            if ( Strings.isNullOrEmpty( key ) )
            {
                return null;
            }

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
    }
}
