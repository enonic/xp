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
 * JWS compact serialization with HMAC-SHA512, pinned to that one algorithm so a verifier cannot
 * be talked into {@code none} or a weaker MAC. Small enough that core needs no JWT library. The
 * header names what the token is for ({@code typ}), and a verifier states what it expects, so a
 * token of one kind never passes as another.
 */
final class JwsHs512
{
    static final String ALG = "HS512";

    /**
     * The token type, RFC 9068's {@code at+jwt}: no other kind of JWT passes as an access token.
     * Verification also accepts the {@code application/} prefix, as RFC 7515 allows.
     */
    static final String TYP = "at+jwt";

    private static final String MAC_ALGORITHM = "HmacSHA512";

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private JwsHs512()
    {
    }

    /**
     * Signs an access token payload under the key, naming it in the header.
     */
    static String sign( final SigningKey key, final ObjectNode payload )
    {
        return sign( key, TYP, payload );
    }

    /**
     * Signs the payload under the key as a token of the given type, naming both in the header.
     */
    static String sign( final SigningKey key, final String typ, final ObjectNode payload )
    {
        final ObjectNode header = MAPPER.createObjectNode();
        header.put( "alg", ALG );
        header.put( "typ", typ );
        header.put( "kid", key.kid() );

        final String signingInput = encode( header ) + "." + encode( payload );
        return signingInput + "." + ENCODER.encodeToString( mac( key, signingInput ) );
    }

    /**
     * Splits a token into its parts and parses the header and payload, verifying nothing. Empty if
     * the token is not a three-part JWS with JSON objects in the first two parts.
     */
    static Optional<Parsed> parse( final String token )
    {
        final int first = token.indexOf( '.' );
        final int second = first < 0 ? -1 : token.indexOf( '.', first + 1 );
        if ( second < 0 || token.indexOf( '.', second + 1 ) >= 0 )
        {
            return Optional.empty();
        }
        final String encodedHeader = token.substring( 0, first );
        final String encodedPayload = token.substring( first + 1, second );
        final String encodedSignature = token.substring( second + 1 );
        try
        {
            final JsonNode header = MAPPER.readTree( DECODER.decode( encodedHeader ) );
            final JsonNode payload = MAPPER.readTree( DECODER.decode( encodedPayload ) );
            if ( !header.isObject() || !payload.isObject() )
            {
                return Optional.empty();
            }
            return Optional.of( new Parsed( token.substring( 0, second ), DECODER.decode( encodedSignature ), (ObjectNode) header,
                                            (ObjectNode) payload ) );
        }
        catch ( Exception _ )
        {
            return Optional.empty();
        }
    }

    /**
     * Whether the header names HS512, the {@code at+jwt} type (any case, with or without the
     * {@code application/} prefix) and no critical extension. Checked before any key is looked up.
     */
    static boolean hasExpectedHeader( final Parsed parsed )
    {
        return hasExpectedHeader( parsed, TYP );
    }

    /**
     * Whether the header names HS512, the given type (any case, with or without the
     * {@code application/} prefix) and no critical extension.
     */
    static boolean hasExpectedHeader( final Parsed parsed, final String expectedTyp )
    {
        final JsonNode alg = parsed.header().get( "alg" );
        final JsonNode typ = parsed.header().get( "typ" );
        return alg != null && alg.isTextual() && ALG.equals( alg.asText() ) && typ != null && typ.isTextual() &&
            isType( typ.asText(), expectedTyp ) && !parsed.header().has( "crit" );
    }

    private static boolean isType( final String typ, final String expected )
    {
        final String type = typ.regionMatches( true, 0, "application/", 0, 12 ) ? typ.substring( 12 ) : typ;
        return expected.equalsIgnoreCase( type );
    }

    /**
     * Checks an access token's signature under the given key, in constant time. The header must
     * also pass {@link #hasExpectedHeader(Parsed)}.
     */
    static boolean verify( final Parsed parsed, final SigningKey key )
    {
        return verify( parsed, key, TYP );
    }

    /**
     * Checks the signature of a token of the given type under the given key, in constant time.
     */
    static boolean verify( final Parsed parsed, final SigningKey key, final String typ )
    {
        return hasExpectedHeader( parsed, typ ) && MessageDigest.isEqual( mac( key, parsed.signingInput() ), parsed.signature() );
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
