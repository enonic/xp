package com.enonic.xp.cluster.impl;

import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Maps;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class ClusterConfigImplTest
{

    @Test
    public void discovery_options_separated()
    {
        final Map<String, String> settings = Maps.newHashMap();
        settings.put( "discovery.some.option", "fish" );
        settings.put( "discovery.some.other.option", "onion" );
        settings.put( "some.option", "cheese" );

        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );

        assertTrue( config.discoveryConfig().exists( "some.option" ) );
        assertTrue( config.discoveryConfig().exists( "some.other.option" ) );
        assertEquals( "fish", config.discoveryConfig().get( "some.option" ) );
        assertEquals( "onion", config.discoveryConfig().get( "some.other.option" ) );
    }

    @Test
    public void name()
    {
        final Map<String, String> settings = Maps.newHashMap();
        settings.put( "node.name", "fish" );
        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );
        assertNotNull( config.name() );
        assertEquals( "fish", config.name().toString() );
    }

    @Test
    public void generated_name()
    {
        final Map<String, String> settings = Maps.newHashMap();
        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );
        assertNotNull( config.name() );
    }

    @Test
    public void enabled_default()
    {
        final Map<String, String> settings = Maps.newHashMap();
        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );
        assertFalse( config.isEnabled() );
    }

    @Test
    public void enabled()
    {
        final Map<String, String> settings = Maps.newHashMap();
        settings.put( "cluster.enabled", "true" );
        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );
        assertTrue( config.isEnabled() );
    }

    @Test
    public void session_replication()
    {
        final Map<String, String> settings = Maps.newHashMap();
        settings.put( "session.replication.enabled", "true" );
        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.activate( settings );
        assertTrue( config.isSessionReplicationEnabled() );
    }

    // network.publish.host
    // network.host

    @Test
    public void network_publish_host()
    {
        final NetworkInterfaceResolver networkInterfaceResolver = Mockito.mock( NetworkInterfaceResolver.class );
        Mockito.when( networkInterfaceResolver.resolveAddress( Mockito.any() ) ).thenReturn( "localhost" );

        final Map<String, String> settings = Maps.newHashMap();

        final ClusterConfigImpl config = new ClusterConfigImpl();
        config.setNetworkInterfaceResolver( networkInterfaceResolver );
        config.activate( settings );
        assertEquals( "localhost", config.networkPublishHost() );
        assertEquals( "localhost", config.networkHost() );
    }


}