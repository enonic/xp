package com.enonic.xp.core.internal.orderkey;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderKeyTokenCodecTest
{
    private static final byte[] REFERENCE_SUBKEY =
        { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

    private final OrderKeyTokenCodec codec = new OrderKeyTokenCodec(
        OrderKeyTokenCodec.deriveSubkey( "master-key".getBytes( StandardCharsets.UTF_8 ), "orderkey-token-v1" ) );

    @Test
    void golden_token_matches_independent_reference_implementation()
    {
        final OrderKeyTokenCodec reference = new OrderKeyTokenCodec( REFERENCE_SUBKEY );
        assertEquals( "1a1b2c3.node-id~011349dec28a58b9", reference.mint( "a1b2c3.node-id", "parent" ) );
    }

    @Test
    void roundtrip()
    {
        final String key = "a1b2c3.node-id";
        assertEquals( key, codec.parse( codec.mint( key, "parent-id" ), "parent-id" ) );
    }

    @Test
    void tag_verification_is_case_insensitive()
    {
        final String token = codec.mint( "a1b2c3.node-id", "parent-id" );
        final int tail = token.length() - 16;
        final String upper = token.substring( 0, tail ) + token.substring( tail ).toUpperCase();
        assertEquals( "a1b2c3.node-id", codec.parse( upper, "parent-id" ) );
    }

    @Test
    void rejects_wrong_scope()
    {
        final String token = codec.mint( "a1b2c3.node-id", "parent-id" );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( token, "other-parent" ) );
    }

    @Test
    void rejects_tampered_key()
    {
        final String token = codec.mint( "a1b2c3.node-id", "parent-id" );
        final String tampered = token.replace( "a1b2c3", "b1b2c3" );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( tampered, "parent-id" ) );
    }

    @Test
    void rejects_tampered_tag()
    {
        final String token = codec.mint( "a1b2c3.node-id", "parent-id" );
        final char last = token.charAt( token.length() - 1 );
        final String tampered = token.substring( 0, token.length() - 1 ) + ( last == '0' ? '1' : '0' );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( tampered, "parent-id" ) );
    }

    @Test
    void rejects_structural_garbage()
    {
        assertThrows( IllegalArgumentException.class, () -> codec.parse( null, "p" ) );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( "", "p" ) );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( "garbage", "p" ) );
        final String token = codec.mint( "a1b2c3.node-id", "p" );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( token.substring( 0, token.length() - 1 ), "p" ) );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( '2' + token.substring( 1 ), "p" ) );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( "1" + "a".repeat( 600 ) + "~0011223344556677", "p" ) );
    }

    @Test
    void rejects_token_from_a_different_subkey()
    {
        final OrderKeyTokenCodec other = new OrderKeyTokenCodec(
            OrderKeyTokenCodec.deriveSubkey( "other-master".getBytes( StandardCharsets.UTF_8 ), "orderkey-token-v1" ) );
        final String token = other.mint( "a1b2c3.node-id", "parent-id" );
        assertThrows( IllegalArgumentException.class, () -> codec.parse( token, "parent-id" ) );
    }

    @Test
    void subkey_derivation_is_deterministic_and_context_separated()
    {
        final byte[] master = "master-key".getBytes( StandardCharsets.UTF_8 );
        final byte[] first = OrderKeyTokenCodec.deriveSubkey( master, "orderkey-token-v1" );
        final byte[] again = OrderKeyTokenCodec.deriveSubkey( master, "orderkey-token-v1" );
        final byte[] other = OrderKeyTokenCodec.deriveSubkey( master, "other-context" );
        assertEquals( 16, first.length );
        assertEquals( java.util.Arrays.toString( first ), java.util.Arrays.toString( again ) );
        assertFalse( java.util.Arrays.equals( first, other ) );
    }

    @Test
    void mint_refuses_invalid_keys()
    {
        assertThrows( IllegalArgumentException.class, () -> codec.mint( "a0.x", "p" ) );
        assertThrows( IllegalArgumentException.class, () -> codec.mint( "no-separator", "p" ) );
    }
}
