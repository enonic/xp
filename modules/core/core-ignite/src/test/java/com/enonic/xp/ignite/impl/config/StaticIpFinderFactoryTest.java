package com.enonic.xp.ignite.impl.config;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterConfig;

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
    public void port_range()
        throws Exception
    {
        Mockito.when( this.igniteSettings.discovery_tcp_port() ).thenReturn( 45000 );
        Mockito.when( this.igniteSettings.discovery_tcp_port_range() ).thenReturn( 5 );
        Mockito.when( this.clusterConfig.networkHost() ).thenReturn( "localhost" );

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

        final TcpDiscoveryVmIpFinder finder = createFinder();

        assertEquals( 15, finder.getRegisteredAddresses().size() );

        assertHost( finder, "192.168.0.1", 45000, 45001, 45002, 45003, 45004 );
        assertHost( finder, "localhost", 45000, 45001, 45002, 45003, 45004 );
        assertHost( finder, "10.0.0.1", 45000, 45001, 45002, 45003, 45004 );
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
            discovery( () -> {
                final InetAddress local1;
                final InetAddress local2;
                try
                {
                    local1 = InetAddress.getByName( "localhost" );
                    local2 = InetAddress.getByName( "192.168.0.1" );
                    return Lists.newArrayList( local1, local2 );
                }
                catch ( UnknownHostException e )
                {
                    throw new RuntimeException();
                }
            } ).
            igniteConfig( igniteSettings ).
            clusterConfig( clusterConfig ).
            build().
            execute();
    }
}