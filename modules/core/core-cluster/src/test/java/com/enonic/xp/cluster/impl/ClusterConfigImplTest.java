package com.enonic.xp.cluster.impl;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import com.enonic.xp.cluster.NodeDiscovery;

import static org.junit.Assert.*;

public class ClusterConfigImplTest
{
    @Test
    public void discovery()
        throws Exception
    {
        final ClusterConfigImpl config = new ClusterConfigImpl();

        final Map<String, String> settings = Maps.newHashMap();
        settings.put( "discovery.unicast.hosts", "localhost, 192.168.0.1, beast.enonic.net" );
        config.activate( settings );

        final NodeDiscovery discovery = config.discovery();
        assertNotNull( discovery );

        final List hosts = discovery.get();
        assertEquals( 3, hosts.size() );
    }

    @Test
    public void default_name()
        throws Exception
    {
        final Map<String, String> settings = Maps.newHashMap();
        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );

        assertNotNull( config.name() );
    }
}