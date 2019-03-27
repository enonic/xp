package com.enonic.xp.elasticsearch.impl;

import java.io.File;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Map;

import org.elasticsearch.common.settings.Settings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;
import com.enonic.xp.cluster.NodeDiscovery;

import static org.junit.Assert.*;

@Ignore
public class NodeSettingsBuilderTest
{
    private NodeSettingsBuilder builder;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup()
        throws Exception
    {
        final BundleContext context = Mockito.mock( BundleContext.class );
        final NodeDiscovery nodeDiscovery = Mockito.mock( NodeDiscovery.class );
        final InetAddress inetAddress = Mockito.mock( InetAddress.class );
        Mockito.when( inetAddress.getCanonicalHostName() ).thenReturn( "127.0.0.1" );
        Mockito.when( nodeDiscovery.get() ).thenReturn( Collections.singletonList( inetAddress ) );
        this.builder = new NodeSettingsBuilder( context, new ClusterConfig()
        {
            @Override
            public NodeDiscovery discovery()
            {
                return nodeDiscovery;
            }

            @Override
            public ClusterNodeId name()
            {
                return ClusterNodeId.from( "local-node" );
            }

            @Override
            public boolean isEnabled()
            {
                return true;
            }

            @Override
            public String networkPublishHost()
            {
                return "127.0.0.1";
            }

            @Override
            public String networkHost()
            {
                return "127.0.0.1";
            }

            @Override
            public boolean isSessionReplicationEnabled()
            {
                return true;
            }
        } );

        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );
    }

    @Test
    public void settings_default()
    {
        final Map<String, String> map = Maps.newHashMap();
        final Settings settings = this.builder.buildSettings( map );

        assertNotNull( settings );
        assertSettings( System.getProperty( "xp.home" ) + "/repo/index", settings );
    }

    @Test
    public void settings_override()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "path", "/to/some/other/path" );

        final Settings settings = this.builder.buildSettings( map );

        assertNotNull( settings );
        //    assertEquals( 23, settings.getAsMap().size() );
        assertSettings( "/to/some/other/path", settings );
    }

    private void assertSettings( String pathValue, final Settings settings )
    {
        assertEquals( "local-node", settings.get( "node.name" ) );
        assertEquals( "false", settings.get( "node.client" ) );
        assertEquals( "true", settings.get( "node.data" ) );
        assertEquals( "true", settings.get( "node.master" ) );
        assertEquals( "false", settings.get( "http.enabled" ) );
        assertEquals( "mycluster", settings.get( "cluster.name" ) );
        assertEquals( "127.0.0.1", settings.get( "network.host" ) );
        assertEquals( "false", settings.get( "discovery.zen.ping.multicast.enabled" ) );
        assertEquals( "127.0.0.1[9300]", settings.get( "discovery.zen.ping.unicast.hosts" ) );
        assertEquals( "1", settings.get( "gateway.expected_nodes" ) );
        assertEquals( "5m", settings.get( "gateway.recover_after_time" ) );
        assertEquals( "1", settings.get( "gateway.recover_after_nodes" ) );
        assertEquals( "1", settings.get( "discovery.zen.minimum_master_nodes" ) );
        assertEquals( "false", settings.get( "cluster.routing.allocation.disk.threshold_enabled" ) );
        assertEquals( "1", settings.get( "index.recovery.initial_shards" ) );
        assertEquals( pathValue, settings.get( "path" ) );
        assertEquals( pathValue + "/data", settings.get( "path.data" ) );
        assertEquals( pathValue + "/work", settings.get( "path.work" ) );
        assertEquals( pathValue + "/conf", settings.get( "path.conf" ) );
        assertEquals( pathValue + "/logs", settings.get( "path.logs" ) );
        assertEquals( pathValue + "/plugins", settings.get( "path.plugins" ) );
    }
}
