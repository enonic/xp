package com.enonic.xp.core.impl.security;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CryptoServiceImplTest
{
    private static final byte[] MATERIAL = "stored-key-material".getBytes( StandardCharsets.UTF_8 );

    @Test
    void no_encryption_key_passes_material_through()
    {
        assertArrayEquals( MATERIAL, CryptoServiceImpl.deriveKey( null, MATERIAL, "token-sig", "token-signing-hs512" ) );
    }

    @Test
    void derivation_is_deterministic_within_an_environment()
    {
        final byte[] kek = "prod-kek".getBytes( StandardCharsets.UTF_8 );
        final byte[] a = CryptoServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "token-signing-hs512" );
        final byte[] b = CryptoServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "token-signing-hs512" );
        assertArrayEquals( a, b );
    }

    @Test
    void same_material_different_encryption_key_yields_different_keys()
    {
        // The QA-copied-from-prod scenario: identical stored material, different KEK in config.
        final byte[] prod = CryptoServiceImpl.deriveKey( "prod-kek".getBytes( StandardCharsets.UTF_8 ), MATERIAL, "token-sig", "k" );
        final byte[] qa = CryptoServiceImpl.deriveKey( "qa-kek".getBytes( StandardCharsets.UTF_8 ), MATERIAL, "token-sig", "k" );
        assertFalse( Arrays.equals( prod, qa ) );
    }

    @Test
    void different_use_or_kid_yields_different_keys()
    {
        final byte[] kek = "kek".getBytes( StandardCharsets.UTF_8 );
        final byte[] base = CryptoServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "k1" );
        assertFalse( Arrays.equals( base, CryptoServiceImpl.deriveKey( kek, MATERIAL, "other-use", "k1" ) ) );
        assertFalse( Arrays.equals( base, CryptoServiceImpl.deriveKey( kek, MATERIAL, "token-sig", "k2" ) ) );
    }

    @Test
    void derived_key_is_full_length()
    {
        final byte[] derived = CryptoServiceImpl.deriveKey( "kek".getBytes( StandardCharsets.UTF_8 ), MATERIAL, "token-sig", "k" );
        assertTrue( derived.length == 64 ); // HMAC-SHA512 output
    }
}
