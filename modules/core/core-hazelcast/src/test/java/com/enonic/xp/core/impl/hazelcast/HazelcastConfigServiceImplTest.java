package com.enonic.xp.core.impl.hazelcast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.config.Config;
import com.hazelcast.config.RestEndpointGroup;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory;
import com.hazelcast.spi.properties.GroupProperty;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.NodeDiscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
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

    private void configureDefaults()
        throws UnknownHostException
    {
        lenient().when( clusterConfig.networkHost() ).thenReturn( "127.0.0.1" );
        lenient().when( clusterConfig.networkPublishHost() ).thenReturn( "127.0.0.1" );
        final NodeDiscovery nodeDiscovery = mock( NodeDiscovery.class );
        lenient().when( nodeDiscovery.get() ).thenReturn( List.of( InetAddress.getByName( "127.0.0.1" ) ) );
        lenient().when( clusterConfig.discovery() ).thenReturn( nodeDiscovery );
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
                   () -> assertIterableEquals( List.of( "127.0.0.1" ), config.getNetworkConfig().getJoin().getTcpIpConfig().getMembers() ),
                   () -> assertNull( config.getNetworkConfig().getRestApiConfig() ) );
    }

    @Test
    void configure_interfaces()
        throws Exception
    {
        configureDefaults();

        when( hazelcastConfig.network_interfaces_enabled() ).thenReturn( true );
        when( hazelcastConfig.network_interfaces() ).thenReturn( "127.0.0.1,127.0.0.2" );
        final Config config = hazelcastConfigService.configure();
        assertAll( () -> assertTrue( config.getNetworkConfig().getInterfaces().isEnabled() ),
                   () -> assertThat( config.getNetworkConfig().getInterfaces().getInterfaces() ).containsExactlyInAnyOrder( "127.0.0.1",
                                                                                                                            "127.0.0.2" ) );
    }

    @Test
    void configure_restClient_enabled()
        throws Exception
    {
        configureDefaults();

        when( hazelcastConfig.network_restApi_enabled() ).thenReturn( true );
        when( hazelcastConfig.network_restApi_restEndpointGroups() ).thenReturn( "WAN, DATA" );
        final Config config = hazelcastConfigService.configure();
        assertAll( () -> assertTrue( config.getNetworkConfig().getRestApiConfig().isEnabled() ),
                   () -> assertEquals( Set.of( RestEndpointGroup.WAN, RestEndpointGroup.DATA ),
                                       config.getNetworkConfig().getRestApiConfig().getEnabledGroups() ) );
    }

    @Test
    void configure_kubernetes_enabled()
        throws Exception
    {
        configureDefaults();

        when( hazelcastConfig.network_join_kubernetes_enabled() ).thenReturn( true );
        when( hazelcastConfig.network_join_kubernetes_serviceDns() ).thenReturn( "some.service.local" );
        final Config config = hazelcastConfigService.configure();
        assertAll( () -> assertEquals( "true", config.getProperty( GroupProperty.DISCOVERY_SPI_ENABLED.getName() ) ),
                   () -> assertTrue( config.getNetworkConfig().getJoin().getDiscoveryConfig().getDiscoveryStrategyConfigs().
                       stream().
                       anyMatch(
                           dsc -> dsc.getDiscoveryStrategyFactory().getClass() == HazelcastKubernetesDiscoveryStrategyFactory.class ) ) );
    }

    @Test
    void configure_clusterConfigDefaults_disabled()
    {
        when( hazelcastConfig.clusterConfigDefaults() ).thenReturn( false );
        when( hazelcastConfig.network_join_tcpIp_members() ).thenReturn( "127.0.0.2, 127.0.0.3" );

        final Config config = hazelcastConfigService.configure();

        assertAll( () -> assertNull( config.getProperty( "hazelcast.local.localAddress" ) ),
                   () -> assertNull( config.getProperty( "hazelcast.local.publicAddress" ) ),
                   () -> assertEquals( "true", config.getProperty( GroupProperty.SOCKET_BIND_ANY.getName() ) ),
                   () -> assertIterableEquals( List.of( "127.0.0.2", "127.0.0.3" ),
                                               config.getNetworkConfig().getJoin().getTcpIpConfig().getMembers() ) );
    }
}
