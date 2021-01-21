package com.enonic.xp.core.internal;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.security.MessageDigests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexCoderTest
{
    @Test
    void toHex()
    {
        assertEquals( "00000001", HexCoder.toHex( new byte[]{0, 0, 0, 1} ) );
        assertEquals( "807f", HexCoder.toHex( new byte[]{Byte.MIN_VALUE, Byte.MAX_VALUE} ) );
        assertEquals(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
            HexCoder.toHex( MessageDigests.sha512().digest() ) );
    }

    @Test
    void toHexLong()
    {
        assertAll( () -> assertEquals( "0000000000000000", HexCoder.toHex( 0 ) ),
                   () -> assertEquals( "0000000000000001", HexCoder.toHex( 1 ) ),
                   () -> assertEquals( "ffffffffffffffff", HexCoder.toHex( -1 ) ),
                   () -> assertEquals( "7fffffffffffffff", HexCoder.toHex( Long.MAX_VALUE ) ),
                   () -> assertEquals( "8000000000000000", HexCoder.toHex( Long.MIN_VALUE ) ) );
    }
}
