package com.enonic.xp.core.impl.hazelcast;

import java.net.InetAddress;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.config.Config;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory;
import com.hazelcast.spi.properties.ClusterProperty;

import com.enonic.xp.cluster.ClusterConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastConfigServiceImplTest
{
    @Mock(stubOnly = true, answer = Answers.RETURNS_DEEP_STUBS)
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
    void configure_clusterConfigDefaults_enabled()
        throws Exception
    {
        when( clusterConfig.networkHost() ).thenReturn( "127.0.1.1" );
        when( clusterConfig.networkPublishHost() ).thenReturn( "127.0.2.1" );
        when( clusterConfig.discovery().get() ).thenReturn(
            List.of( InetAddress.getByName( "127.0.0.1" ), InetAddress.getByName( "127.0.1.1" ) ) );

        final Config config = hazelcastConfigService.configure();

        assertAll( () -> assertEquals( "true", config.getProperty( ClusterProperty.PHONE_HOME_ENABLED.getName() ) ),
                   () -> assertEquals( "true", config.getProperty( ClusterProperty.SOCKET_BIND_ANY.getName() ) ),
                   () -> assertEquals( "127.0.2.1", config.getNetworkConfig().getPublicAddress() ),
                   () -> assertIterableEquals( List.of( "127.0.0.1", "127.0.1.1" ),
                                               config.getNetworkConfig().getJoin().getTcpIpConfig().getMembers() ),
                   () -> assertTrue( config.getNetworkConfig().getInterfaces().isEnabled() ),
                   () -> assertThat( config.getNetworkConfig().getInterfaces().getInterfaces() ).containsExactlyInAnyOrder( "127.0.1.1" ) );
    }

    @Test
    void configure_interfaces()
        throws Exception
    {
        when( clusterConfig.discovery().get() ).thenReturn( List.of( InetAddress.getByName( "127.0.0.1" ) ) );

        when( hazelcastConfig.clusterConfigDefaults() ).thenReturn( false );
        when( hazelcastConfig.network_interfaces_enabled() ).thenReturn( true );
        when( hazelcastConfig.network_interfaces() ).thenReturn( "127.0.0.1,127.0.0.2" );
        final Config config = hazelcastConfigService.configure();
        assertAll( () -> assertTrue( config.getNetworkConfig().getInterfaces().isEnabled() ),
                   () -> assertThat( config.getNetworkConfig().getInterfaces().getInterfaces() ).containsExactlyInAnyOrder( "127.0.0.1",
                                                                                                                            "127.0.0.2" ) );
    }

    @Test
    void configure_partition_group_enabled()
        throws Exception
    {
        when( clusterConfig.networkHost() ).thenReturn( "127.0.0.1" );
        when( clusterConfig.discovery().get() ).thenReturn( List.of( InetAddress.getByName( "127.0.0.1" ) ) );

        when( hazelcastConfig.partition_group_enabled() ).thenReturn( true );
        when( hazelcastConfig.partition_group_groupType() ).thenReturn( "ZONE_AWARE" );
        final Config config = hazelcastConfigService.configure();
        assertAll( () -> assertTrue( config.getPartitionGroupConfig().isEnabled() ),
                   () -> assertEquals( PartitionGroupConfig.MemberGroupType.ZONE_AWARE, config.getPartitionGroupConfig().getGroupType() ) );
    }

    @Test
    void configure_kubernetes_enabled()
    {
        when( hazelcastConfig.clusterConfigDefaults() ).thenReturn( false );
        when( hazelcastConfig.network_join_tcpIp_enabled() ).thenReturn( false );
        when( hazelcastConfig.network_join_kubernetes_enabled() ).thenReturn( true );
        when( hazelcastConfig.network_join_kubernetes_serviceDns() ).thenReturn( "some.service.local" );
        final Config config = hazelcastConfigService.configure();
        assertAll( () -> assertEquals( "true", config.getProperty( ClusterProperty.DISCOVERY_SPI_ENABLED.getName() ) ), () -> assertTrue(
            config.getNetworkConfig()
                .getJoin()
                .getDiscoveryConfig()
                .getDiscoveryStrategyConfigs()
                .stream()
                .anyMatch( dsc -> dsc.getDiscoveryStrategyFactory().getClass() == HazelcastKubernetesDiscoveryStrategyFactory.class ) ) );
    }

    @Test
    void configure_clusterConfigDefaults_disabled()
    {
        when( hazelcastConfig.clusterConfigDefaults() ).thenReturn( false );
        when( hazelcastConfig.network_join_tcpIp_members() ).thenReturn( "127.0.0.2, 127.0.0.3" );

        final Config config = hazelcastConfigService.configure();

        assertAll( () -> assertEquals( "true", config.getProperty( ClusterProperty.SOCKET_BIND_ANY.getName() ) ),
                   () -> assertIterableEquals( List.of( "127.0.0.2", "127.0.0.3" ),
                                               config.getNetworkConfig().getJoin().getTcpIpConfig().getMembers() ) );
    }
}
