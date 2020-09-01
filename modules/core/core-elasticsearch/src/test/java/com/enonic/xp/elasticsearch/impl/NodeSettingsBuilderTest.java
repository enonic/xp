package com.enonic.xp.elasticsearch.impl;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.settings.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterNodeId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NodeSettingsBuilderTest
{
    private NodeSettingsBuilder builder;

    @Mock
    private BundleContext context;

    @Mock(stubOnly = true, lenient = true, answer = Answers.RETURNS_DEEP_STUBS)
    private ClusterConfig clusterConfig;

    @TempDir
    public Path temporaryFolder;

    @BeforeEach
    void setup()
        throws Exception
    {
        final InetAddress inetAddress = mock( InetAddress.class );
        when( inetAddress.getCanonicalHostName() ).thenReturn( "127.0.0.1" );

        when( clusterConfig.isEnabled() ).thenReturn( true );
        when( clusterConfig.discovery().get() ).thenReturn( List.of( inetAddress ) );
        when( clusterConfig.name() ).thenReturn( ClusterNodeId.from( "local-node" ) );
        when( clusterConfig.networkHost() ).thenReturn( "127.0.0.1" );
        when( clusterConfig.networkPublishHost() ).thenReturn( "127.0.0.1" );

        this.builder = new NodeSettingsBuilder( context, clusterConfig );

        final Path homeDir = Files.createDirectory( this.temporaryFolder.resolve( "home" ) ).toAbsolutePath();
        System.setProperty( "xp.home", homeDir.toString() );
    }

    @Test
    void settings_default()
    {
        final Map<String, String> map = new HashMap<>();
        final Settings settings = this.builder.buildSettings( map );

        assertNotNull( settings );
        assertSettings( System.getProperty( "xp.home" ) + "/repo/index", settings );
    }

    @Test
    void settings_override()
    {
        final Map<String, String> map = new HashMap<>();
        map.put( "path", "/to/some/other/path" );

        final Settings settings = this.builder.buildSettings( map );

        assertNotNull( settings );
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
        assertEquals( "127.0.0.1:9300", settings.get( "discovery.zen.ping.unicast.hosts" ) );
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
