package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.spi.discovery.DiscoverySpi;

import com.enonic.xp.cluster.NodeDiscovery;

class DiscoveryFactory
{
    private final NodeDiscovery discovery;

    private final IgniteSettings igniteConfig;

    private DiscoveryFactory( final Builder builder )
    {
        discovery = builder.discovery;
        igniteConfig = builder.igniteConfig;
    }

    DiscoverySpi execute()
    {
        return TcpDiscoveryFactory.create().
            discovery( this.discovery ).
            igniteConfig( this.igniteConfig ).
            build().
            execute();
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

        DiscoveryFactory build()
        {
            return new DiscoveryFactory( this );
        }
    }
}
