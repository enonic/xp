package com.enonic.xp.core.impl.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PHCEncoderTest
{
    private PHCEncoder encoder;

    @BeforeEach
    void setUp()
    {
        this.encoder = new PHCEncoder( "$pbkdf2-sha512$i=210000,l=64,slen=16" );
    }

    @Test
    void encode_verify()
    {
        final String encodedPwd = encoder.encode( "fiskepudding".toCharArray() );

        assertTrue( encodedPwd.startsWith( "$pbkdf2-sha512$" ) );
        assertTrue( encoder.verify( "fiskepudding".toCharArray(), encodedPwd ) );
        assertFalse( encoder.verify( "Fiskepudding".toCharArray(), encodedPwd ) );
        assertFalse( encoder.verify( "fiskepudding1".toCharArray(), encodedPwd ) );
        assertFalse( encoder.verify( "fiskepudding ".toCharArray(), encodedPwd ) );
    }

    @Test
    void verify_preexisting()
    {
        final String preexistingHash = "$pbkdf2-sha512$i=210000,l=64$S/z9ekKo6im8UkB8wouyqw$hPnWhSiQjKYZzx8fcB8hgk66SNxLKq3wqIXWEcxsMgflu8hmFHRkjwzCUSocQhPnNBITDWHiii3rbdYO43Mu/w";

        assertTrue( encoder.verify( "pass123".toCharArray(), preexistingHash ) );
        assertFalse( encoder.verify( "wrongpassword".toCharArray(), preexistingHash ) );
    }

    @Test
    void unknown_algorithm()
    {
        assertThrows( IllegalArgumentException.class,
                      () -> encoder.verify( "fiskepudding".toCharArray(), "$pbkdf2-sha256$i=210000,l=64$c2FsdA$aGFzaA" ) );
    }

    @Test
    void missing_iterations()
    {
        assertThrows( NullPointerException.class,
                      () -> encoder.verify( "fiskepudding".toCharArray(), "$pbkdf2-sha512$l=64$c2FsdA$aGFzaA" ) );
    }

    @Test
    void missing_length()
    {
        assertThrows( NullPointerException.class,
                      () -> encoder.verify( "fiskepudding".toCharArray(), "$pbkdf2-sha512$i=210000$c2FsdA$aGFzaA" ) );
    }
}
