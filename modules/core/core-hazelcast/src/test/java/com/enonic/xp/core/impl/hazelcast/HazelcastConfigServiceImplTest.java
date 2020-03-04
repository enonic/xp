package com.enonic.xp.core.impl.hazelcast;

import java.net.InetAddress;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.config.Config;
import com.hazelcast.spi.properties.GroupProperty;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.NodeDiscovery;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastConfigServiceImplTest
{
    @Mock
    private ClusterConfig clusterConfig;

    private HazelcastConfig hazelcastConfig;

    private HazelcastConfigServiceImpl hazelcastConfigService;

    @BeforeEach
    void setUp()
    {
        hazelcastConfig = mock( HazelcastConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        hazelcastConfigService = new HazelcastConfigServiceImpl( clusterConfig, hazelcastConfig );
    }

    @Test
    void isHazelcastEnabled_enabled()
    {
        when( clusterConfig.isEnabled() ).thenReturn( true );
        assertTrue( hazelcastConfigService.isHazelcastEnabled() );
    }

    @Test
    void isHazelcastEnabled_disabled()
    {
        when( clusterConfig.isEnabled() ).thenReturn( false );
        assertFalse( hazelcastConfigService.isHazelcastEnabled() );
    }

    @Test
    void configure_default()
        throws Exception
    {
        when( clusterConfig.networkHost() ).thenReturn( "127.0.0.1" );
        when( clusterConfig.networkPublishHost() ).thenReturn( "127.0.0.1" );
        final NodeDiscovery nodeDiscovery = mock( NodeDiscovery.class );
        when( nodeDiscovery.get() ).thenReturn( List.of( InetAddress.getByName( "127.0.0.1" ) ) );
        when( clusterConfig.discovery() ).thenReturn( nodeDiscovery );

        final Config config = hazelcastConfigService.configure();

        assertAll( () -> assertEquals( "true", config.getProperty( GroupProperty.PHONE_HOME_ENABLED.getName() ) ),
                   () -> assertEquals( "127.0.0.1", config.getProperty( "hazelcast.local.localAddress" ) ),
                   () -> assertEquals( "127.0.0.1", config.getProperty( "hazelcast.local.publicAddress" ) ),
                   () -> assertEquals( "true", config.getProperty( GroupProperty.SOCKET_BIND_ANY.getName() ) ),
                   () -> assertIterableEquals( List.of( "127.0.0.1" ),
                                               config.getNetworkConfig().getJoin().getTcpIpConfig().getMembers() ) );
    }

    @Test
    void configure_clusterConfigDefaults_disabled()
    {
        when( hazelcastConfig.clusterConfigDefaults() ).thenReturn( false );

        final Config config = hazelcastConfigService.configure();

        assertAll( () -> assertNull( config.getProperty( "hazelcast.local.localAddress" ) ),
                   () -> assertNull( config.getProperty( "hazelcast.local.publicAddress" ) ),
                   () -> assertEquals( "true", config.getProperty( GroupProperty.SOCKET_BIND_ANY.getName() ) ),
                   () -> assertIterableEquals( List.of(), config.getNetworkConfig().getJoin().getTcpIpConfig().getMembers() ) );
    }
}
