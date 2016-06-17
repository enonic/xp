package com.enonic.xp.server.udc.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UdcInfoGeneratorTest
{
    private UdcInfoGenerator generator;

    @Before
    public void setup()
    {
        this.generator = new UdcInfoGenerator();
    }

    @Test
    public void generate()
        throws Exception
    {
        final UdcInfo info = this.generator.generate();
        assertNotNull( info );
        assertNotNull( info.uuid );
        assertNotNull( info.version );
        assertNotNull( info.versionHash );
        assertEquals( "xp", info.product );
        assertTrue( info.numCpu > 0 );
        assertTrue( info.maxMemory > 0 );
        assertNotNull( info.javaVersion );
        assertNotNull( info.osName );
        assertNotNull( "", info.toJson() );
    }
}
