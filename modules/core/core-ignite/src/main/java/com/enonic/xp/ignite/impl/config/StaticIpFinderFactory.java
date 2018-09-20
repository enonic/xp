package com.enonic.xp.ignite.impl.config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.DiscoveryConfig;
import com.enonic.xp.cluster.DiscoveryType;

import static com.enonic.xp.cluster.DiscoveryConfig.UNICAST_HOST_KEY;

class StaticIpFinderFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( StaticIpFinderFactory.class );

    private final IgniteSettings igniteConfig;

    private final ClusterConfig clusterConfig;

    private StaticIpFinderFactory( final Builder builder )
    {
        igniteConfig = builder.igniteConfig;
        clusterConfig = builder.clusterConfig;
    }

    TcpDiscoveryVmIpFinder execute()
    {
        final TcpDiscoveryVmIpFinder staticIpFinder = new TcpDiscoveryVmIpFinder();

        final DiscoveryConfig discoveryConfig = this.clusterConfig.discoveryConfig();

        if ( discoveryConfig.getType().equals( DiscoveryType.STATIC_IP ) )
        {
            return createStaticIpFinder( staticIpFinder, discoveryConfig );
        }

        throw new RuntimeException( "Discovery-type: " + discoveryConfig.getType() + " not implemented" );
    }

    @NotNull
    private TcpDiscoveryVmIpFinder createStaticIpFinder( final TcpDiscoveryVmIpFinder staticIpFinder,
                                                         final DiscoveryConfig discoveryConfig )
    {
        if ( !Strings.isNullOrEmpty( igniteConfig.discovery_hosts() ) )
        {
            return createFromIgniteConfig( staticIpFinder );
        }

        if ( discoveryConfig.exists( UNICAST_HOST_KEY ) )
        {
            return createFromClusterConfig( staticIpFinder, discoveryConfig );
        }

        return createDefaultLocalDiscovery();
    }

    @NotNull
    private TcpDiscoveryVmIpFinder createFromClusterConfig( final TcpDiscoveryVmIpFinder staticIpFinder,
                                                            final DiscoveryConfig discoveryConfig )
    {
        final List<String> hostStrings = createDiscoveryAddressList( discoveryConfig );
        return doSetAddresses( staticIpFinder, hostStrings );
    }

    @NotNull
    private TcpDiscoveryVmIpFinder createFromIgniteConfig( final TcpDiscoveryVmIpFinder staticIpFinder )
    {
        // final Stream<String> localAddresses = getLocalTcpDiscoveryAddress();
        final Stream<String> discoveryAddresses = Stream.of( igniteConfig.discovery_hosts().split( "," ) );
        // final List<String> allAddresses = joinStreams( discoveryAddresses, localAddresses );

        return doSetAddresses( staticIpFinder, discoveryAddresses.collect( Collectors.toList() ) );
    }

    @NotNull
    private TcpDiscoveryVmIpFinder createDefaultLocalDiscovery()
    {
        return doSetAddresses( new TcpDiscoveryVmIpFinder(), getLocalTcpDiscoveryAddress().collect( Collectors.toList() ) );
    }

    private List<String> createDiscoveryAddressList( final DiscoveryConfig discoveryConfig )
    {
        final Stream<String> localAddresses = getLocalTcpDiscoveryAddress();
        final Stream<String> discoveryAddresses = createDiscoveryAddresses( discoveryConfig );

        return joinStreams( localAddresses, discoveryAddresses );
    }

    @NotNull
    private TcpDiscoveryVmIpFinder doSetAddresses( final TcpDiscoveryVmIpFinder staticIpFinder, final List<String> allAddresses )
    {
        staticIpFinder.setAddresses( allAddresses );
        LOG.info( "Setting discovery [staticIP] with address: [" + allAddresses + "]" );
        return staticIpFinder;
    }

    private List<String> joinStreams( final Stream<String> localAddresses, final Stream<String> discoveryAddresses )
    {
        return Stream.concat( discoveryAddresses, localAddresses ).
            distinct().
            collect( Collectors.toList() );
    }

    @NotNull
    private Stream<String> createDiscoveryAddresses( final DiscoveryConfig discoveryConfig )
    {
        return Stream.of( discoveryConfig.get( UNICAST_HOST_KEY ).split( "," ) ).
            map( e -> e + getPortPrefix() );
    }

    @NotNull
    private Stream<String> getLocalTcpDiscoveryAddress()
    {
        final String discoveryTcpLocalAddress = clusterConfig.networkHost();
        return Stream.of( discoveryTcpLocalAddress + getPortPrefix() );
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
        private IgniteSettings igniteConfig;

        private ClusterConfig clusterConfig;

        private Builder()
        {
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
