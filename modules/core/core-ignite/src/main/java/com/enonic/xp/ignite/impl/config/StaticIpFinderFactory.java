package com.enonic.xp.ignite.impl.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.NodeDiscovery;

class StaticIpFinderFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( StaticIpFinderFactory.class );

    private final NodeDiscovery discovery;

    private final IgniteSettings igniteConfig;

    private final ClusterConfig clusterConfig;

    private StaticIpFinderFactory( final Builder builder )
    {
        discovery = builder.discovery;
        igniteConfig = builder.igniteConfig;
        clusterConfig = builder.clusterConfig;
    }

    TcpDiscoveryVmIpFinder execute()
    {
        final TcpDiscoveryVmIpFinder staticIpFinder = new TcpDiscoveryVmIpFinder();

        final String portPrefix = getPortPrefix();

        final String discoveryTcpLocalAddress = clusterConfig.networkHost();
        final Stream<String> configLocalAddress = Stream.of( discoveryTcpLocalAddress + portPrefix );
        final Stream<String> discoveryAddresses = this.discovery.get().stream().map( host -> host.getCanonicalHostName() + portPrefix );
        final List<String> hostStrings = Stream.concat( discoveryAddresses, configLocalAddress ).
            distinct().
            collect( Collectors.toList() );

        staticIpFinder.setAddresses( hostStrings );

        LOG.info( "Setting discovery [staticIP] with address: [" + hostStrings + "]" );

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

        private ClusterConfig clusterConfig;

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

        Builder clusterConfig( final ClusterConfig clusterConfig )
        {
            this.clusterConfig = clusterConfig;
            return this;
        }

        StaticIpFinderFactory build()
        {
            return new StaticIpFinderFactory( this );
        }
    }
}
