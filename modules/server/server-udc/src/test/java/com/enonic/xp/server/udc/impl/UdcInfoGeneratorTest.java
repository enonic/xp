package com.enonic.xp.server.udc.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UdcInfoGeneratorTest
{
    private UdcInfoGenerator generator;

    @BeforeEach
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
