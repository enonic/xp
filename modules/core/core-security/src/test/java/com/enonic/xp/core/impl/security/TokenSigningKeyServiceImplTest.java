package com.enonic.xp.core.impl.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenSigningKeyServiceImplTest
{
    private static final byte[] MATERIAL = "stored-key-material".getBytes( StandardCharsets.UTF_8 );

    @Test
    void no_encryption_key_passes_material_through()
    {
        assertArrayEquals( MATERIAL, TokenSigningKeyServiceImpl.deriveKey( null, MATERIAL, "token-sig", "token-signing-hs512" ) );
    }

    @Test
    void derivation_is_deterministic_within_an_environment()
    {
        final byte[] kek = "prod-kek".getBytes( StandardCharsets.UTF_8 );
        final byte[] a = TokenSigningKeyServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "token-signing-hs512" );
        final byte[] b = TokenSigningKeyServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "token-signing-hs512" );
        assertArrayEquals( a, b );
    }

    @Test
    void same_material_different_encryption_key_yields_different_keys()
    {
        // The QA-copied-from-prod scenario: identical stored material, different KEK in config.
        final byte[] prod = TokenSigningKeyServiceImpl.deriveKey( "prod-kek".getBytes( StandardCharsets.UTF_8 ), MATERIAL, "token-sig", "k" );
        final byte[] qa = TokenSigningKeyServiceImpl.deriveKey( "qa-kek".getBytes( StandardCharsets.UTF_8 ), MATERIAL, "token-sig", "k" );
        assertFalse( Arrays.equals( prod, qa ) );
    }

    @Test
    void different_use_or_kid_yields_different_keys()
    {
        final byte[] kek = "kek".getBytes( StandardCharsets.UTF_8 );
        final byte[] base = TokenSigningKeyServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "k1" );
        assertFalse( Arrays.equals( base, TokenSigningKeyServiceImpl.deriveKey( kek, MATERIAL, "other-use", "k1" ) ) );
        assertFalse( Arrays.equals( base, TokenSigningKeyServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "k2" ) ) );
    }

    @Test
    void derived_key_is_full_length()
    {
        final byte[] derived = TokenSigningKeyServiceImpl.deriveKey( "kek".getBytes( StandardCharsets.UTF_8 ), MATERIAL, "token-sig", "k" );
        assertTrue( derived.length == 64 ); // HMAC-SHA512 output
    }

    @Test
    void newest_key_ranks_highest()
    {
        final Instant older = Instant.parse( "2026-01-01T00:00:00Z" );
        final Instant newer = Instant.parse( "2026-06-01T00:00:00Z" );
        assertTrue( ranksAbove( candidate( newer ), candidate( older ) ) );
        assertFalse( ranksAbove( candidate( older ), candidate( newer ) ) );
    }

    @Test
    void key_with_creation_time_ranks_above_one_without()
    {
        final Instant created = Instant.parse( "2026-01-01T00:00:00Z" );
        assertTrue( ranksAbove( candidate( created ), candidate( null ) ) );
    }

    private static boolean ranksAbove( final TokenSigningKeyServiceImpl.KeyCandidate a, final TokenSigningKeyServiceImpl.KeyCandidate b )
    {
        return TokenSigningKeyServiceImpl.BY_AGE.compare( a, b ) > 0;
    }

    private static TokenSigningKeyServiceImpl.KeyCandidate candidate( final Instant created )
    {
        return new TokenSigningKeyServiceImpl.KeyCandidate( "kid", created );
    }
}
