package com.enonic.xp.ignite.impl.config;

import java.io.File;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.osgi.framework.BundleContext;

import com.google.common.base.Strings;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.home.HomeDir;

public class ConfigurationFactory
{
    private final ClusterConfig clusterConfig;

    private final IgniteSettings igniteConfig;

    private final BundleContext bundleContext;

    private ConfigurationFactory( final Builder builder )
    {
        clusterConfig = builder.clusterConfig;
        igniteConfig = builder.igniteConfig;
        bundleContext = builder.bundleContext;
    }

    public IgniteConfiguration execute()
    {
        final IgniteConfiguration config = new IgniteConfiguration();

        config.setIgniteInstanceName( InstanceNameResolver.resolve() );
        config.setConsistentId( clusterConfig.name().toString() );
        config.setIgniteHome( resolveIgniteHome() );

        if ( !igniteConfig.connector_enabled() )
        {
            config.setConnectorConfiguration( null );
        }

        if ( !igniteConfig.odbc_enabled() )
        {
            config.setClientConnectorConfiguration( null );
        }

        if ( !Strings.isNullOrEmpty( igniteConfig.localhost() ) )
        {
            config.setLocalHost( igniteConfig.localhost() );
        }

        config.setDiscoverySpi( DiscoveryFactory.create().
            discovery( clusterConfig.discovery() ).
            igniteConfig( igniteConfig ).
            build().
            execute() );

        config.setGridLogger( LoggerConfig.create() );

        config.setMetricsLogFrequency( igniteConfig.metrics_log_frequency() );

        config.setClassLoader( ClassLoaderFactory.create( this.bundleContext ) );

        return config;
    }

    private String resolveIgniteHome()
    {
        if ( Strings.isNullOrEmpty( igniteConfig.home() ) )
        {
            return HomeDir.get().toFile().getPath();
        }

        return new File( this.igniteConfig.home() ).getPath();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ClusterConfig clusterConfig;

        private IgniteSettings igniteConfig;

        private BundleContext bundleContext;

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

        public Builder bundleContext( final BundleContext val )
        {
            bundleContext = val;
            return this;
        }

        public ConfigurationFactory build()
        {
            return new ConfigurationFactory( this );
        }
    }
}
