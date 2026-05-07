package com.enonic.xp.core.internal;

import java.security.SecureRandom;
import java.util.UUID;

public final class UuidHelper
{
    private static final SecureRandom RANDOM = new SecureRandom();

    private UuidHelper()
    {
    }

    public static UUID newUUIDv7()
    {
        final long timestamp = System.currentTimeMillis();

        final byte[] random = new byte[10];
        RANDOM.nextBytes( random );

        long msb = ( timestamp << 16 ) | ( ( random[0] & 0xFF ) << 8 ) | ( random[1] & 0xFF );
        msb = ( msb & 0xFFFF_FFFF_FFFF_0FFFL ) | 0x0000_0000_0000_7000L;

        long lsb = 0;
        for ( int i = 2; i < 10; i++ )
        {
            lsb = ( lsb << 8 ) | ( random[i] & 0xFF );
        }
        lsb = ( lsb & 0x3FFF_FFFF_FFFF_FFFFL ) | 0x8000_0000_0000_0000L;

        return new UUID( msb, lsb );
    }
}
