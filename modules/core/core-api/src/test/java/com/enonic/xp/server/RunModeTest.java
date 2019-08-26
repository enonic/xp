package com.enonic.xp.server;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RunModeTest
{
    @Test
    public void testInfo()
        throws Exception
    {
        final Properties props = new Properties();

        props.put( "xp.runMode", "prod" );
        assertEquals( RunMode.PROD, RunMode.get( props ) );

        props.put( "xp.runMode", "PROD" );
        assertEquals( RunMode.PROD, RunMode.get( props ) );

        props.put( "xp.runMode", "dev" );
        assertEquals( RunMode.DEV, RunMode.get( props ) );

        props.put( "xp.runMode", "DEV" );
        assertEquals( RunMode.DEV, RunMode.get( props ) );

        props.put( "xp.runMode", "other" );
        assertEquals( RunMode.PROD, RunMode.get( props ) );
    }
}
