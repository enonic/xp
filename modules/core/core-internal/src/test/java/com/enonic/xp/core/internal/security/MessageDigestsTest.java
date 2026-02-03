package com.enonic.xp.core.internal.security;

import java.util.HexFormat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageDigestsTest
{

    @Test
    void sha512()
    {
        assertEquals(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
            HexFormat.of().formatHex( MessageDigests.sha512().digest() ) );
    }

    @Test
    void sha256()
    {
        assertEquals( "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                      HexFormat.of().formatHex( MessageDigests.sha256().digest() ) );
    }
}