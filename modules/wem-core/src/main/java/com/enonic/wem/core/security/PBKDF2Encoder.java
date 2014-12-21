package com.enonic.wem.core.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.google.common.base.Strings;

import com.enonic.wem.api.util.Exceptions;

public class PBKDF2Encoder
    implements PasswordEncoder
{
    public static final String SEPARATOR = ":";

    public static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    public static final int HASH_BYTE_SIZE = 24;

    public static final int PBKDF2_ITERATIONS = 1000;

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
        final byte[] encodedPwd;

        encodedPwd = encodePassword( plainPassword.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE );

        final String saltAsString = toHex( salt );
        final String encodedAsString = toHex( encodedPwd );
        return generateTypedString( encodedAsString, saltAsString );
    }

    @Override
    public boolean validate( final String key, final String correctPasswordKey )
    {
        final ExtractedKey extractedPwd = ExtractedKey.from( correctPasswordKey );

        final String type = extractedPwd.type;
        if ( !type.equals( this.getType() ) )
        {
            throw new IllegalArgumentException( "Incorrect type of encryption, expected '" + this.getType() + "', got '" + type + "'" );
        }

        final byte[] correctHash = fromHex( extractedPwd.pwd );
        final byte[] generatedHash = encodePassword( key.toCharArray(), fromHex( extractedPwd.salt ), PBKDF2_ITERATIONS, HASH_BYTE_SIZE );

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

    private byte[] encodePassword( final char[] password, byte[] salt, final int iterations, final int bytes )
    {
        try
        {
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, bytes * 8 );
            SecretKeyFactory skf = SecretKeyFactory.getInstance( ALGORITHM );
            return skf.generateSecret( spec ).getEncoded();
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

    private static String toHex( byte[] array )
    {
        BigInteger bi = new BigInteger( 1, array );
        String hex = bi.toString( 16 );
        int paddingLength = ( array.length * 2 ) - hex.length();
        if ( paddingLength > 0 )
        {
            return String.format( "%0" + paddingLength + "d", 0 ) + hex;
        }
        else
        {
            return hex;
        }
    }

    private static byte[] fromHex( String hex )
    {
        byte[] binary = new byte[hex.length() / 2];
        for ( int i = 0; i < binary.length; i++ )
        {
            binary[i] = (byte) Integer.parseInt( hex.substring( 2 * i, 2 * i + 2 ), 16 );
        }
        return binary;
    }

    private static class ExtractedKey
    {
        private final String salt;

        private final String pwd;

        private final String type;

        private ExtractedKey( final String salt, final String pwd, final String type )
        {
            this.salt = salt;
            this.pwd = pwd;
            this.type = type;
        }

        public static ExtractedKey from( final String key )
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

            return new ExtractedKey( salt, pwd, name );
        }
    }
}
