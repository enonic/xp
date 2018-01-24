package com.enonic.xp.ignite.impl;

import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

public class DiscoveryFactory
{

    public static DiscoverySpi create()
    {
        final TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        final TcpDiscoveryVmIpFinder staticIpFinder = createStaticIpFinder();
        discoverySpi.setIpFinder( staticIpFinder );

        return discoverySpi;
    }

    @NotNull
    private static TcpDiscoveryVmIpFinder createStaticIpFinder()
    {
        final TcpDiscoveryVmIpFinder staticIpFinder = new TcpDiscoveryVmIpFinder();
        staticIpFinder.setAddresses( Lists.newArrayList( "127.0.0.1" ) );
        return staticIpFinder;
    }

}
