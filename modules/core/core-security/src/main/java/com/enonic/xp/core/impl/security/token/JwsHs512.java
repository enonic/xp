package com.enonic.xp.core.impl.security.token;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;

/**
 * JWS compact serialization with HMAC-SHA512, which is all the token service needs: one algorithm,
 * pinned, so an attacker cannot talk the verifier into {@code none} or a weaker MAC. Small enough
 * that no JWT library needs to enter core.
 */
final class JwsHs512
{
    static final String ALG = "HS512";

    /**
     * The token type named in the header and required on verification. The plain JWT type is
     * enough because of an invariant the service keeps: a signing key signs access tokens and
     * nothing else, so no other JWT under the same key exists to be taken for one.
     */
    static final String TYP = "JWT";

    private static final String MAC_ALGORITHM = "HmacSHA512";

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private JwsHs512()
    {
    }

    /**
     * Signs the payload under the key, naming it in the header.
     */
    static String sign( final SigningKey key, final ObjectNode payload )
    {
        final ObjectNode header = MAPPER.createObjectNode();
        header.put( "alg", ALG );
        header.put( "typ", TYP );
        header.put( "kid", key.kid() );

        final String signingInput = encode( header ) + "." + encode( payload );
        return signingInput + "." + ENCODER.encodeToString( mac( key, signingInput ) );
    }

    /**
     * Splits a token into its parts and parses the header and payload, without verifying anything.
     * Empty when the token is not a three-part JWS with JSON objects in the first two parts.
     */
    static Optional<Parsed> parse( final String token )
    {
        final String[] parts = token.split( "\\.", -1 );
        if ( parts.length != 3 )
        {
            return Optional.empty();
        }
        try
        {
            final JsonNode header = MAPPER.readTree( DECODER.decode( parts[0] ) );
            final JsonNode payload = MAPPER.readTree( DECODER.decode( parts[1] ) );
            if ( !header.isObject() || !payload.isObject() )
            {
                return Optional.empty();
            }
            return Optional.of( new Parsed( parts[0] + "." + parts[1], DECODER.decode( parts[2] ), (ObjectNode) header,
                                            (ObjectNode) payload ) );
        }
        catch ( Exception _ )
        {
            return Optional.empty();
        }
    }

    /**
     * Whether the header names the one algorithm and the token type this serialization accepts,
     * and no critical extension: none is supported, and RFC 7515 says an unsupported one must fail
     * the token. Checked before any key is looked up; the type comparison ignores case, as JWT
     * requires.
     */
    static boolean hasExpectedHeader( final Parsed parsed )
    {
        final JsonNode alg = parsed.header().get( "alg" );
        final JsonNode typ = parsed.header().get( "typ" );
        return alg != null && alg.isTextual() && ALG.equals( alg.asText() ) && typ != null && typ.isTextual() &&
            TYP.equalsIgnoreCase( typ.asText() ) && !parsed.header().has( "crit" );
    }

    /**
     * Checks the signature of a parsed token under the key the header names, comparing in constant
     * time. The header must pass {@link #hasExpectedHeader} too.
     */
    static boolean verify( final Parsed parsed, final SigningKey key )
    {
        return hasExpectedHeader( parsed ) && MessageDigest.isEqual( mac( key, parsed.signingInput() ), parsed.signature() );
    }

    private static byte[] mac( final SigningKey key, final String signingInput )
    {
        try
        {
            final Mac mac = Mac.getInstance( MAC_ALGORITHM );
            mac.init( new SecretKeySpec( key.secret(), MAC_ALGORITHM ) );
            return mac.doFinal( signingInput.getBytes( StandardCharsets.US_ASCII ) );
        }
        catch ( GeneralSecurityException e )
        {
            throw new IllegalStateException( e );
        }
    }

    private static String encode( final ObjectNode node )
    {
        try
        {
            return ENCODER.encodeToString( MAPPER.writeValueAsBytes( node ) );
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( e );
        }
    }

    static ObjectNode newPayload()
    {
        return MAPPER.createObjectNode();
    }

    record Parsed(String signingInput, byte[] signature, ObjectNode header, ObjectNode payload)
    {
        Optional<String> kid()
        {
            final JsonNode kid = header.get( "kid" );
            return kid == null || !kid.isTextual() ? Optional.empty() : Optional.of( kid.asText() );
        }
    }
}
