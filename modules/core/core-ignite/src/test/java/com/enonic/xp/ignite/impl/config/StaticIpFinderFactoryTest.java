package com.enonic.xp.ignite.impl.config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;

import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.DiscoveryConfig;

import static org.junit.Assert.*;

public class StaticIpFinderFactoryTest
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
    public void ignite_config_override_cluster_config()
        throws Exception
    {
        Mockito.when( this.igniteSettings.discovery_hosts() ).thenReturn( "10.1.2.3:47501,10.1.2.4:47502" );
        Mockito.when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 45000 );
        Mockito.when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 5 );
        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "localhost" );
        Mockito.when( this.clusterConfig.discoveryConfig() ).thenReturn( DiscoveryConfig.create().
            add( DiscoveryConfig.UNICAST_HOST_KEY, "192.168.0.1,localhost" ).
            build() );

        final TcpDiscoveryVmIpFinder finder = createFinder();

        System.out.println( finder.getRegisteredAddresses() );

    }

    @Test
    public void port_range()
        throws Exception
    {
        Mockito.when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 45000 );
        Mockito.when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 5 );
        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "localhost" );
        Mockito.when( this.clusterConfig.discoveryConfig() ).thenReturn( DiscoveryConfig.create().
            add( DiscoveryConfig.UNICAST_HOST_KEY, "192.168.0.1,localhost" ).
            build() );

        final TcpDiscoveryVmIpFinder finder = createFinder();

        assertEquals( 10, finder.getRegisteredAddresses().size() );

        assertHost( finder, "192.168.0.1", 45000, 45001, 45002, 45003, 45004 );
        assertHost( finder, "localhost", 45000, 45001, 45002, 45003, 45004 );
    }

    @Test
    public void single_port()
        throws Exception
    {
        Mockito.when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 45000 );
        Mockito.when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 0 );
        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "localhost" );
        Mockito.when( this.clusterConfig.discoveryConfig() ).thenReturn( DiscoveryConfig.create().
            add( DiscoveryConfig.UNICAST_HOST_KEY, "192.168.0.1,localhost" ).
            build() );

        final TcpDiscoveryVmIpFinder finder = createFinder();

        assertEquals( 2, finder.getRegisteredAddresses().size() );

        assertHost( finder, "192.168.0.1", 45000 );
        assertHost( finder, "localhost", 45000 );

    }

    @Test
    public void test_discovery_tcp_localAddress()
        throws Exception
    {
        Mockito.when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 45000 );
        Mockito.when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 5 );
        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "10.0.0.1" );
        Mockito.when( this.clusterConfig.discoveryConfig() ).thenReturn( DiscoveryConfig.create().
            add( DiscoveryConfig.UNICAST_HOST_KEY, "192.168.0.1,localhost" ).
            build() );

        final TcpDiscoveryVmIpFinder finder = createFinder();

        assertEquals( 15, finder.getRegisteredAddresses().size() );

        assertHost( finder, "192.168.0.1", 45000, 45001, 45002, 45003, 45004 );
        assertHost( finder, "localhost", 45000, 45001, 45002, 45003, 45004 );
        assertHost( finder, "10.0.0.1", 45000, 45001, 45002, 45003, 45004 );
    }

    @Test
    public void no_settings_given()
    {
        Mockito.when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 45000 );
        Mockito.when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 5 );
        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "localhost" );
        Mockito.when( this.clusterConfig.discoveryConfig() ).thenReturn( DiscoveryConfig.create().
            build() );

        final TcpDiscoveryVmIpFinder finder = createFinder();

    }

    private void assertHost( final TcpDiscoveryVmIpFinder finder, final String host, final int... ports )
        throws Exception
    {
        final Collection<InetSocketAddress> registeredAddresses = finder.getRegisteredAddresses();

        final InetAddress inetAddress = InetAddress.getByName( host );

        for ( final int port : ports )
        {
            assertTrue( registeredAddresses.contains( new InetSocketAddress( inetAddress, port ) ) );
        }
    }

    private TcpDiscoveryVmIpFinder createFinder()
    {
        return StaticIpFinderFactory.create().
            igniteConfig( igniteSettings ).
            clusterConfig( clusterConfig ).
            build().
            execute();
    }
}