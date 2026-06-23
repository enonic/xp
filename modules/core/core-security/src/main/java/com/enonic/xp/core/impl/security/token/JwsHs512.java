package com.enonic.xp.core.impl.security.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Minimal HS512 (HMAC-SHA512) JWS reader/writer. Kept dependency-free (no external JWT
 * library in core) and deliberately small: it signs and verifies compact JWS; all claim
 * semantics (exp/iss/aud) are enforced by the caller.
 */
final class JwsHs512
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Base64.Encoder B64URL = Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder B64URL_DEC = Base64.getUrlDecoder();

    private static final String HMAC_SHA512 = "HmacSHA512";

    private JwsHs512()
    {
    }

    static String sign( final Map<String, Object> header, final Map<String, Object> claims, final SecretKey key )
    {
        try
        {
            final String signingInput = encode( MAPPER.writeValueAsBytes( header ) ) + "." + encode( MAPPER.writeValueAsBytes( claims ) );
            final String signature = encode( mac( key, signingInput ) );
            return signingInput + "." + signature;
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "Unable to sign token", e );
        }
    }

    /**
     * Verifies the signature and returns the decoded claims, or {@code null} if the token is
     * malformed or the signature does not match.
     */
    static Map<String, Object> verify( final String token, final SecretKey key )
    {
        try
        {
            final int firstDot = token.indexOf( '.' );
            final int lastDot = token.lastIndexOf( '.' );
            if ( firstDot <= 0 || lastDot <= firstDot )
            {
                return null;
            }

            final String signingInput = token.substring( 0, lastDot );
            final byte[] expected = mac( key, signingInput );
            final byte[] actual = B64URL_DEC.decode( token.substring( lastDot + 1 ) );
            if ( !MessageDigest.isEqual( expected, actual ) )
            {
                return null;
            }

            return parseSegment( token.substring( firstDot + 1, lastDot ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    /**
     * Decodes a JWS segment (e.g. the header) without any verification.
     */
    static Map<String, Object> peekSegment( final String token, final int index )
    {
        try
        {
            final String[] parts = token.split( "\\.", -1 );
            if ( index >= parts.length )
            {
                return null;
            }
            return parseSegment( parts[index] );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> parseSegment( final String segment )
        throws Exception
    {
        return MAPPER.readValue( B64URL_DEC.decode( segment ), Map.class );
    }

    private static byte[] mac( final SecretKey key, final String signingInput )
        throws Exception
    {
        final Mac mac = Mac.getInstance( HMAC_SHA512 );
        mac.init( key );
        return mac.doFinal( signingInput.getBytes( StandardCharsets.UTF_8 ) );
    }

    private static String encode( final byte[] bytes )
    {
        return B64URL.encodeToString( bytes );
    }
}
