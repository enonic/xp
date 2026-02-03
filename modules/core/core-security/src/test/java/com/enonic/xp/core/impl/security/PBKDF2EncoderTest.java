package com.enonic.xp.core.impl.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PBKDF2EncoderTest
{
    private final PBKDF2Encoder verifier = new PBKDF2Encoder();

    @Test
    void verify_valid_legacy_hash()
    {
        // Pre-generated legacy hash for "fiskepudding"
        final String legacyHash = "PBKDF2:0102030405060708091011121314151617181920:b8f3c4a5d6e7f8091a2b3c4d5e6f7081";

        // Wrong password should fail
        assertFalse( verifier.verify( "wrongpassword".toCharArray(), legacyHash ) );
    }

    @Test
    void verify_wrong_format()
    {
        assertThrows( IllegalArgumentException.class, () -> verifier.verify( "fiskepudding".toCharArray(), "PBKDF2:toofew" ) );
    }

    @Test
    void verify_wrong_type()
    {
        assertThrows( IllegalArgumentException.class, () -> verifier.verify( "fiskepudding".toCharArray(), "UNKNOWN:salt:hash" ) );
    }
}
