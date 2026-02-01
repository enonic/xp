package com.enonic.xp.core.impl.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Supplier;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.jspecify.annotations.NullMarked;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;

@NullMarked
final class PHCEncoder
    implements PasswordEncoder, PasswordVerifier
{
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final String PHC_ID = "pbkdf2-sha512";

    private static final Base64.Encoder PHC_B64 = Base64.getEncoder().withoutPadding();

    private final int keyLength;

    private final int iterations;

    private final int saltLength;

    private final SecureRandom secureRandom = new SecureRandom();

    private final Supplier<String> dummyHash = Suppliers.memoize( () -> encode( "__dummy__".toCharArray() ) );

    PHCEncoder( final String policy )
    {
        var policyData = PHCParser.parse( policy );
        if ( !PHC_ID.equals( policyData.id() ) )
        {
            throw new IllegalArgumentException( "Unsupported PHC algorithm id in policy: " + policyData.id() );
        }
        this.keyLength = policyData.paramInt( "l" );
        this.iterations = policyData.paramInt( "i" );
        this.saltLength = policyData.paramInt( "slen" );
    }

    @Override
    public String encode( final char[] plainPassword )
    {
        final byte[] salt = new byte[saltLength];
        secureRandom.nextBytes( salt );

        final byte[] hash = hashPassword( plainPassword, salt, this.iterations, this.keyLength );

        return "$" + PHC_ID + "$i=" + this.iterations + ",l=" + this.keyLength + "$" + PHC_B64.encodeToString( salt ) + "$" +
            PHC_B64.encodeToString( hash );
    }

    @Override
    public boolean verify( final char[] plainPassword, final String phc )
    {
        final PHCParser.PHCData phcData = PHCParser.parse( Objects.requireNonNullElseGet( Strings.emptyToNull( phc ), this.dummyHash ) );

        if ( !PHC_ID.equals( phcData.id() ) )
        {
            throw new IllegalArgumentException( "Unsupported PHC algorithm id: " + phcData.id() );
        }

        int keyLength = phcData.paramInt( "l" );
        int iterations = phcData.paramInt( "i" );

        final byte[] generatedHash = hashPassword( plainPassword, phcData.salt(), iterations, keyLength );

        // order matters. First check the hash, then check for empty password to avoid timing attacks
        return MessageDigest.isEqual( phcData.hash(), generatedHash ) && !Strings.isNullOrEmpty( phc );
    }

    private byte[] hashPassword( final char[] password, byte[] salt, final int iterations, final int keyLength )
    {
        try
        {
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength * 8 );
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( ALGORITHM );
            return keyFactory.generateSecret( spec ).getEncoded();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}
