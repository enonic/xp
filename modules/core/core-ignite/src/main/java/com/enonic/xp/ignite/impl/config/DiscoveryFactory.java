package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.spi.discovery.DiscoverySpi;

import com.enonic.xp.cluster.ClusterConfig;

class DiscoveryFactory
{
    private final IgniteSettings igniteConfig;

    private final ClusterConfig clusterConfig;

    private DiscoveryFactory( final Builder builder )
    {
        igniteConfig = builder.igniteConfig;
        clusterConfig = builder.clusterConfig;
    }

    DiscoverySpi execute()
    {
        return TcpDiscoveryFactory.create().
            igniteConfig( this.igniteConfig ).
            clusterConfig( this.clusterConfig ).
            build().
            execute();
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

        DiscoveryFactory build()
        {
            return new DiscoveryFactory( this );
        }
    }
}
