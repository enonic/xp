package com.enonic.xp.core.impl.security;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;

import com.enonic.xp.core.internal.security.MessageDigests;

@NullMarked
public class SuPasswordVerifier
    implements PasswordVerifier
{
    private static final Logger LOG = LoggerFactory.getLogger( SuPasswordVerifier.class );

    private static final Pattern SU_PASSWORD_PATTERN = Pattern.compile( "\\{(sha256|sha512)}(\\S+)", Pattern.CASE_INSENSITIVE );

    private static final String SU_PASSWORD_PROPERTY_KEY = "xp.suPassword";

    private final byte @Nullable [] correctHash;

    private final Supplier<MessageDigest> digestSupplier;

    String encodedPassword = System.getProperty( SU_PASSWORD_PROPERTY_KEY, "" );

    public SuPasswordVerifier()
    {
        if ( encodedPassword.isEmpty() )
        {
            this.correctHash = null;
            this.digestSupplier = () -> null;
            LOG.warn( SU_PASSWORD_PROPERTY_KEY + " is not set" );
            return;
        }

        final Matcher suPasswordMatcher = SU_PASSWORD_PATTERN.matcher( encodedPassword );
        if ( suPasswordMatcher.find() )
        {
            final String alg = suPasswordMatcher.group( 1 );
            this.digestSupplier = switch ( alg )
            {
                case "sha256" -> MessageDigests::sha256;
                case "sha512" -> MessageDigests::sha512;
                default -> throw new IllegalArgumentException( "Unsupported hash algorithm for " + SU_PASSWORD_PROPERTY_KEY + ": " + alg );
            };

            correctHash = HexFormat.of().parseHex( suPasswordMatcher.group( 2 ) );
        }
        else
        {
            this.correctHash = null;
            this.digestSupplier = () -> null;
            LOG.warn( "Invalid " + SU_PASSWORD_PROPERTY_KEY + " format" );
        }
    }

    @Override
    public boolean verify( final char[] plainPassword, final @Nullable String unused )
    {
        if ( this.correctHash == null )
        {
            // Fail fast since there is no benefit to hide from timing attacks that su password is not set
            return false;
        }
        byte[] generatedHash = hashPassword( plainPassword );
        return MessageDigest.isEqual( this.correctHash, generatedHash );
    }

    private byte[] hashPassword( final char[] plainPassword )
    {
        final MessageDigest digest = this.digestSupplier.get();
        digest.update( StandardCharsets.UTF_8.encode( CharBuffer.wrap( plainPassword ) ) );
        return digest.digest();
    }
}
