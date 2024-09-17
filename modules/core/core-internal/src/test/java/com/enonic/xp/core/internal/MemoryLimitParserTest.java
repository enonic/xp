package com.enonic.xp.core.internal;

import java.util.function.LongSupplier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryLimitParserTest
{

    @Test
    void jvmMemory()
    {
        final LongSupplier baselineSupplier = () -> 1024;
        assertEquals( (long) Math.rint( 1024 / 10. ), new MemoryLimitParser( baselineSupplier ).parse( "10%" ) );
        assertEquals( (long) Math.rint( 1024 / 1.5 ), new MemoryLimitParser( baselineSupplier ).parse( "1.5%" ) );
    }

    @Test
    void gigabytes()
    {
        assertEquals( 10L * 1024 * 1024 * 1024, new MemoryLimitParser( () -> 0 ).parse( "10GB" ) );
    }

}
