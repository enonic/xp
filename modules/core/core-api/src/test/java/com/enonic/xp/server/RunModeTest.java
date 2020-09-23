package com.enonic.xp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RunModeTest
{
    @Test
    void testInfo()
    {
        assertEquals( RunMode.PROD, RunMode.get( "prod" ) );

        assertEquals( RunMode.PROD, RunMode.get( "PROD" ) );

        assertEquals( RunMode.DEV, RunMode.get( "dev" ) );

        assertEquals( RunMode.DEV, RunMode.get( "DEV" ) );

        assertEquals( RunMode.PROD, RunMode.get( "other" ) );

        assertEquals( RunMode.PROD, RunMode.get( null ) );
    }
}
