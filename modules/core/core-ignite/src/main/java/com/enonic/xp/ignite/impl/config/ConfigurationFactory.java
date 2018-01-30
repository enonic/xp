package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.configuration.IgniteConfiguration;

import com.enonic.xp.cluster.ClusterConfig;

public class ConfigurationFactory
{
    private final ClusterConfig clusterConfig;

    private final IgniteSettings igniteConfig;

    private ConfigurationFactory( final Builder builder )
    {
        clusterConfig = builder.clusterConfig;
        igniteConfig = builder.igniteConfig;
    }

    public IgniteConfiguration execute()
    {
        final IgniteConfiguration config = new IgniteConfiguration();

        config.setIgniteInstanceName( InstanceNameResolver.resolve() );

        config.setDiscoverySpi( DiscoveryFactory.create().
            discovery( clusterConfig.discovery() ).
            igniteConfig( igniteConfig ).
            build().
            execute() );

        config.setConsistentId( clusterConfig.name().toString() );

        return config;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ClusterConfig clusterConfig;

        private IgniteSettings igniteConfig;

        private Builder()
        {
        }

        public Builder clusterConfig( final ClusterConfig val )
        {
            clusterConfig = val;
            return this;
        }

        public Builder igniteConfig( final IgniteSettings val )
        {
            igniteConfig = val;
            return this;
        }

        public ConfigurationFactory build()
        {
            return new ConfigurationFactory( this );
        }
    }
}
