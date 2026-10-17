package com.enonic.xp.core.impl.security.token;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.enonic.xp.core.internal.security.MessageDigests;

/**
 * The refresh token format and what is derived from a token.
 * <p>
 * A token is {@code <secret>.<grant id>.<generation>.<tag>}: 32 random bytes in base64url, the id
 * of the grant node it redeems, so redeeming is one node read, the number of the rotation that
 * issued it, and an HMAC over the three under the grant's own key. The tag proves the token was
 * issued for the grant, so a retired token is recognised as such however long ago it was retired,
 * while a token merely naming the grant is not, and cannot get the grant revoked. Only the secret
 * is secret: the grant id is also in every access token, and the grant's key proves provenance,
 * not possession. Stored of the current token is the SHA-256 of the whole token.
 * <p>
 * A rotation's successor token is kept for the grace period wrapped under the token it replaced
 * and a secret the node store does not hold: AES-GCM with a key derived from both. A repeated
 * redeem of the retired token gets the same successor back, and neither the store, nor the store
 * together with the retired token, yields it.
 */
final class RefreshTokens
{
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder B64D = Base64.getUrlDecoder();

    /**
     * The one spelling of a token: the generation is a canonical decimal, so the string hashed as
     * the token's identity and the string the tag signs are the same bytes.
     */
    private static final Pattern TOKEN =
        Pattern.compile( "(([A-Za-z0-9_-]{43})\\.([A-Za-z0-9_-]{1,64})\\.(0|[1-9][0-9]{0,17}))\\.([A-Za-z0-9_-]{43})" );

    private static final String HMAC = "HmacSHA256";

    private static final int SECRET_BYTES = 32;

    private static final int KEY_BYTES = 32;

    private static final int IV_BYTES = 12;

    private static final int TAG_BITS = 128;

    private static final byte[] GRACE_KEY_DOMAIN = "grace:".getBytes( StandardCharsets.UTF_8 );

    private RefreshTokens()
    {
    }

    /**
     * The parts of a well-formed token, with the signing input as presented: the tag is checked
     * over the presented bytes, never over a reconstruction.
     */
    record Parts(String signingInput, String grantId, long generation, String tag)
    {
    }

    static byte[] newGrantKey()
    {
        final byte[] key = new byte[KEY_BYTES];
        RANDOM.nextBytes( key );
        return key;
    }

    static String newToken( final byte[] grantKey, final String grantId, final long generation )
    {
        final byte[] secret = new byte[SECRET_BYTES];
        RANDOM.nextBytes( secret );
        final String signingInput = B64.encodeToString( secret ) + "." + grantId + "." + generation;
        return signingInput + "." + B64.encodeToString( hmac( grantKey, signingInput ) );
    }

    static Optional<Parts> parse( final String token )
    {
        final Matcher matcher = TOKEN.matcher( token );
        return matcher.matches()
            ? Optional.of( new Parts( matcher.group( 1 ), matcher.group( 3 ), Long.parseLong( matcher.group( 4 ) ), matcher.group( 5 ) ) )
            : Optional.empty();
    }

    /**
     * Whether the token's tag is the grant key's signature of its other parts: whether the token
     * was issued for the grant holding the key.
     */
    static boolean isIssuedUnder( final Parts parts, final byte[] grantKey )
    {
        return MessageDigest.isEqual( hmac( grantKey, parts.signingInput() ), B64D.decode( parts.tag() ) );
    }

    static String hash( final String token )
    {
        final MessageDigest digest = MessageDigests.sha256();
        digest.update( token.getBytes( StandardCharsets.UTF_8 ) );
        return B64.encodeToString( digest.digest() );
    }

    static String wrap( final byte[] clusterSecret, final String presented, final String next )
    {
        final byte[] iv = new byte[IV_BYTES];
        RANDOM.nextBytes( iv );
        try
        {
            final Cipher cipher = Cipher.getInstance( "AES/GCM/NoPadding" );
            cipher.init( Cipher.ENCRYPT_MODE, graceKey( clusterSecret, presented ), new GCMParameterSpec( TAG_BITS, iv ) );
            final byte[] sealed = cipher.doFinal( next.getBytes( StandardCharsets.UTF_8 ) );
            final byte[] out = new byte[iv.length + sealed.length];
            System.arraycopy( iv, 0, out, 0, iv.length );
            System.arraycopy( sealed, 0, out, iv.length, sealed.length );
            return B64.encodeToString( out );
        }
        catch ( GeneralSecurityException e )
        {
            throw new IllegalStateException( e );
        }
    }

    static Optional<String> unwrap( final byte[] clusterSecret, final String presented, final String wrapped )
    {
        try
        {
            final byte[] in = B64D.decode( wrapped );
            if ( in.length <= IV_BYTES )
            {
                return Optional.empty();
            }
            final Cipher cipher = Cipher.getInstance( "AES/GCM/NoPadding" );
            cipher.init( Cipher.DECRYPT_MODE, graceKey( clusterSecret, presented ), new GCMParameterSpec( TAG_BITS, in, 0, IV_BYTES ) );
            return Optional.of( new String( cipher.doFinal( in, IV_BYTES, in.length - IV_BYTES ), StandardCharsets.UTF_8 ) );
        }
        catch ( GeneralSecurityException | IllegalArgumentException e )
        {
            return Optional.empty();
        }
    }

    private static SecretKeySpec graceKey( final byte[] clusterSecret, final String presented )
    {
        final byte[] input = new byte[GRACE_KEY_DOMAIN.length + presented.length()];
        System.arraycopy( GRACE_KEY_DOMAIN, 0, input, 0, GRACE_KEY_DOMAIN.length );
        System.arraycopy( presented.getBytes( StandardCharsets.UTF_8 ), 0, input, GRACE_KEY_DOMAIN.length, presented.length() );
        return new SecretKeySpec( hmac( clusterSecret, input ), "AES" );
    }

    private static byte[] hmac( final byte[] key, final String input )
    {
        return hmac( key, input.getBytes( StandardCharsets.UTF_8 ) );
    }

    private static byte[] hmac( final byte[] key, final byte[] input )
    {
        try
        {
            final Mac mac = Mac.getInstance( HMAC );
            mac.init( new SecretKeySpec( key, HMAC ) );
            return mac.doFinal( input );
        }
        catch ( GeneralSecurityException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
