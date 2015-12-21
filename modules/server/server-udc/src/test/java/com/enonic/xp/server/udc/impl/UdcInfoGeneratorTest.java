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
        assertNotNull( info.getVersion() );
        assertNotNull( info.getVersionHash() );
        assertEquals( "xp", info.getProduct() );
        assertNotNull( info.getHardwareAddress() );
        assertTrue( info.getNumCpu() > 0 );
        assertTrue( info.getMaxMemory() > 0 );
        assertNotNull( info.getJavaVersion() );
        assertNotNull( info.getOsName() );
        assertEquals( 1, info.getCount() );

        final UdcInfo info2 = this.generator.generate();
        assertEquals( 2, info2.getCount() );
    }
}
