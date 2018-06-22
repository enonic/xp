package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.cluster.ClusterConfig;

import static org.junit.Assert.*;

public class TcpDiscoveryFactoryTest
{
    private IgniteSettings igniteSettings;

    private ClusterConfig clusterConfig;

    @Before
    public void setUp()
        throws Exception
    {
        this.igniteSettings = Mockito.mock( IgniteSettings.class );
        this.clusterConfig = Mockito.mock( ClusterConfig.class );
    }

    @Test
    public void config_values()
        throws Exception
    {
        Mockito.when( igniteSettings.discovery_tcp_ack_timeout() ).thenReturn( 3000L );
        Mockito.when( igniteSettings.discovery_tcp_network_timeout() ).thenReturn( 4000L );
        Mockito.when( igniteSettings.discovery_tcp_join_timeout() ).thenReturn( 5000L );
        Mockito.when( igniteSettings.discovery_tcp_socket_timeout() ).thenReturn( 6000L );
        Mockito.when( igniteSettings.discovery_tcp_port() ).thenReturn( 1234 );
        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "localhost" );

        final TcpDiscoverySpi tcpDiscoverySpi = TcpDiscoveryFactory.create().
            discovery( new TestDiscovery( "localhost", "192.168.0.1" ) ).
            igniteConfig( this.igniteSettings ).
            clusterConfig( this.clusterConfig ).
            build().
            execute();

        assertEquals( 3000L, tcpDiscoverySpi.getAckTimeout() );
        assertEquals( 4000L, tcpDiscoverySpi.getNetworkTimeout() );
        assertEquals( 5000L, tcpDiscoverySpi.getJoinTimeout() );
        assertEquals( 6000L, tcpDiscoverySpi.getSocketTimeout() );
        assertEquals( "localhost", tcpDiscoverySpi.getLocalAddress() );
        // assertEquals( 1234, tcpDiscoverySpi.getLocalPort() );

    }
}