package com.enonic.xp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RunModeTest
{
    @Test
    void testInfo()
    {
        RunMode.set( RunMode.PROD );
        assertEquals( RunMode.PROD, RunMode.get() );

        RunMode.set( RunMode.DEV );
        assertEquals( RunMode.DEV, RunMode.get() );
    }
}
