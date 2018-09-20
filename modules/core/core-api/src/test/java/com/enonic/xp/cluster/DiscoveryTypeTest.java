package com.enonic.xp.cluster;

import org.junit.Test;

import static org.junit.Assert.*;

public class DiscoveryTypeTest
{

    @Test
    public void fromString()
    {
        assertEquals( DiscoveryType.STATIC_IP, DiscoveryType.fromString( "staticIp" ) );
        assertEquals( DiscoveryType.STATIC_IP, DiscoveryType.fromString( "staticIP" ) );
        assertEquals( DiscoveryType.STATIC_IP, DiscoveryType.fromString( "staticip" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid()
    {
        assertEquals( DiscoveryType.STATIC_IP, DiscoveryType.fromString( "fisk" ) );

    }
}