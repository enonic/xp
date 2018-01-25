package com.enonic.xp.ignite.impl.config;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

import com.enonic.xp.cluster.NodeDiscovery;

class StaticIpFinderFactory
{
    private final NodeDiscovery discovery;

    private final IgniteSettings igniteConfig;

    private StaticIpFinderFactory( final Builder builder )
    {
        discovery = builder.discovery;
        igniteConfig = builder.igniteConfig;
    }

    TcpDiscoveryVmIpFinder execute()
    {
        final TcpDiscoveryVmIpFinder staticIpFinder = new TcpDiscoveryVmIpFinder();

        final String portPrefix = getPortPrefix();

        final List<String> hostStrings =
            this.discovery.get().stream().map( host -> host.getCanonicalHostName() + portPrefix ).collect( Collectors.toList() );

        staticIpFinder.setAddresses( hostStrings );
        return staticIpFinder;
    }

    private String getPortPrefix()
    {
        final int portRange = this.igniteConfig.discovery_tcp_port_range();
        final int port = this.igniteConfig.discovery_tcp_port();

        if ( portRange == 0 )
        {
            return ":" + port;
        }

        return ":" + port + ".." + ( port + ( portRange - 1 ) );
    }

    static Builder create()
    {
        return new Builder();
    }

    static final class Builder
    {
        private NodeDiscovery discovery;

        private IgniteSettings igniteConfig;

        private Builder()
        {
        }

        Builder discovery( final NodeDiscovery val )
        {
            discovery = val;
            return this;
        }

        Builder igniteConfig( final IgniteSettings val )
        {
            igniteConfig = val;
            return this;
        }

        StaticIpFinderFactory build()
        {
            return new StaticIpFinderFactory( this );
        }
    }
}
