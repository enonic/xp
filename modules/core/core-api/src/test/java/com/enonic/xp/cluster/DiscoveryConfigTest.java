package com.enonic.xp.cluster;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class DiscoveryConfigTest
{
    @Test
    public void static_ip_type()
    {
        final Map<String, String> settings = Maps.newHashMap();
        settings.put( DiscoveryConfig.DISCOVERY_TYPE_KEY, DiscoveryType.STATIC_IP.toString() );

        final DiscoveryConfig config = new DiscoveryConfig( settings );
        assertEquals( DiscoveryType.STATIC_IP, config.getType() );
    }


    @Test(expected = IllegalArgumentException.class)
    public void invalid_type()
    {
        final Map<String, String> settings = Maps.newHashMap();
        settings.put( DiscoveryConfig.DISCOVERY_TYPE_KEY, "dummy" );

        new DiscoveryConfig( settings );
    }
}
