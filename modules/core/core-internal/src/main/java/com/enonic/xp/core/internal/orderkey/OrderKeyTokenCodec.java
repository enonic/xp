package com.enonic.xp.core.internal.orderkey;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Wraps order keys into the tokens that leave the server and verifies the tokens that come back. The key itself is
 * stored and indexed plain; only the wire form carries a tag, so a client can replay positions it was handed - anchors
 * for a reorder - but cannot mint one, which keeps exact placement values out of reach of standard means. The tag binds
 * the scope the token was issued under, so a token cannot wander to another parent, and the leading version character
 * lets the scheme change without anything stored to migrate.
 * <p>
 * The tag is a truncated HMAC-SHA512 under a subkey derived once from a master key, the same construction the redirect
 * checksums use; the master key is never spent per token, and the derivation separates this use of it from theirs.
 */
public final class OrderKeyTokenCodec
{
    private static final char VERSION = '1';

    private static final char TAG_SEPARATOR = '~';

    private static final int TAG_HEX_LENGTH = 16;

    private static final int TAG_BYTES = TAG_HEX_LENGTH / 2;

    private static final String ALGORITHM = "HmacSHA512";

    private static final int MAX_TOKEN_LENGTH =
        2 + OrderKeyCodec.MAX_POSITION_LENGTH + 1 + OrderKeyCodec.MAX_DISCRIMINATOR_LENGTH + 1 + TAG_HEX_LENGTH;

    private final SecretKeySpec subkey;

    public OrderKeyTokenCodec( final byte[] subkey )
    {
        if ( subkey == null || subkey.length != 16 )
        {
            throw new IllegalArgumentException( "Order key token subkey must be 16 bytes" );
        }
        this.subkey = new SecretKeySpec( subkey, ALGORITHM );
    }

    /**
     * Derives the token subkey from a master key, so the master key itself is used once per JVM rather than once per
     * token, and a derived key cannot be walked back to it.
     */
    public static byte[] deriveSubkey( final byte[] masterKey, final String context )
    {
        try
        {
            final Mac mac = Mac.getInstance( ALGORITHM );
            mac.init( new SecretKeySpec( masterKey, ALGORITHM ) );
            final byte[] derived = mac.doFinal( context.getBytes( StandardCharsets.UTF_8 ) );
            final byte[] subkey = new byte[16];
            System.arraycopy( derived, 0, subkey, 0, 16 );
            return subkey;
        }
        catch ( NoSuchAlgorithmException | InvalidKeyException e )
        {
            throw new IllegalStateException( e );
        }
    }

    public String mint( final String key, final String scope )
    {
        OrderKeyCodec.requireValidKey( key );
        return VERSION + key + TAG_SEPARATOR + HexFormat.of().formatHex( tag( key, scope ) );
    }

    /**
     * Verifies a token and returns the key it carries. Rejection reports what failed structurally but never which tag
     * was expected.
     */
    public String parse( final String token, final String scope )
    {
        if ( token == null || token.length() > MAX_TOKEN_LENGTH )
        {
            throw new IllegalArgumentException( "Invalid order key token" );
        }
        if ( token.length() < 1 + 1 + 1 + 1 + TAG_HEX_LENGTH || token.charAt( 0 ) != VERSION )
        {
            throw new IllegalArgumentException( "Invalid order key token" );
        }
        final int tagStart = token.length() - TAG_HEX_LENGTH;
        if ( token.charAt( tagStart - 1 ) != TAG_SEPARATOR )
        {
            throw new IllegalArgumentException( "Invalid order key token" );
        }

        final String key = token.substring( 1, tagStart - 1 );
        OrderKeyCodec.requireValidKey( key );

        final byte[] expected = HexFormat.of().formatHex( tag( key, scope ) ).getBytes( StandardCharsets.ISO_8859_1 );
        final byte[] presented = token.substring( tagStart ).toLowerCase().getBytes( StandardCharsets.ISO_8859_1 );
        if ( !MessageDigest.isEqual( expected, presented ) )
        {
            throw new IllegalArgumentException( "Order key token rejected" );
        }
        return key;
    }

    private byte[] tag( final String key, final String scope )
    {
        final byte[] scopeBytes = scope.getBytes( StandardCharsets.UTF_8 );
        final byte[] keyBytes = key.getBytes( StandardCharsets.UTF_8 );
        final byte[] message = new byte[1 + scopeBytes.length + 1 + keyBytes.length];
        message[0] = VERSION;
        System.arraycopy( scopeBytes, 0, message, 1, scopeBytes.length );
        System.arraycopy( keyBytes, 0, message, 1 + scopeBytes.length + 1, keyBytes.length );
        try
        {
            final Mac mac = Mac.getInstance( ALGORITHM );
            mac.init( subkey );
            return Arrays.copyOf( mac.doFinal( message ), TAG_BYTES );
        }
        catch ( NoSuchAlgorithmException | InvalidKeyException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
