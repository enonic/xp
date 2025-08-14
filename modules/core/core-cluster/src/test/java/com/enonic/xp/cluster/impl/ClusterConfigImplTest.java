package com.enonic.xp.cluster.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.cluster.NodeDiscovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClusterConfigImplTest
{
    @Test
    public void discovery()
        throws Exception
    {
        final ClusterConfigImpl config = new ClusterConfigImpl();

        final Map<String, String> settings = new HashMap<>();
        settings.put( "discovery.unicast.hosts", "localhost, 192.168.0.1" );
        config.activate( settings );

        final NodeDiscovery discovery = config.discovery();
        assertNotNull( discovery );

        final List hosts = discovery.get();
        assertEquals( 2, hosts.size() );
    }

    @Test
    public void default_name()
        throws Exception
    {
        final Map<String, String> settings = new HashMap<>();
        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );

        assertNotNull( config.name() );
    }
}
