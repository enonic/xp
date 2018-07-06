package com.enonic.xp.ignite.impl.config;

import java.io.File;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.configuration.AddressResolver;
import org.apache.ignite.configuration.BasicAddressResolver;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.marshaller.optimized.OptimizedMarshaller;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.home.HomeDir;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class ConfigurationFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( ConfigurationFactory.class );

    private final ClusterConfig clusterConfig;

    private final IgniteSettings igniteSettings;

    private final BundleContext bundleContext;

    private ConfigurationFactory( final Builder builder )
    {
        clusterConfig = builder.clusterConfig;
        igniteSettings = builder.igniteConfig;
        bundleContext = builder.bundleContext;
    }

    public IgniteConfiguration execute()
    {
        final IgniteConfiguration config = new IgniteConfiguration();

        config.setIgniteInstanceName( InstanceNameResolver.resolve() );
        config.setConsistentId( clusterConfig.name().toString() );
        config.setIgniteHome( resolveIgniteHome() );
        config.setAddressResolver( getAddressResolver() );
        config.setMarshaller( new OptimizedMarshaller( true ) );

        config.setDataStorageConfiguration( DataStorageConfigFactory.create( this.igniteSettings ) );

        if ( !igniteSettings.connector_enabled() )
        {
            config.setConnectorConfiguration( null );
        }

        if ( !igniteSettings.odbc_enabled() )
        {
            config.setClientConnectorConfiguration( null );
        }

        if ( !Strings.isNullOrEmpty( igniteSettings.localhost() ) )
        {
            config.setLocalHost( igniteSettings.localhost() );
        }

        config.setDiscoverySpi( DiscoveryFactory.create().
            discovery( clusterConfig.discovery() ).
            igniteConfig( igniteSettings ).
            clusterConfig( clusterConfig ).
            build().
            execute() );

        config.setCommunicationSpi( CommunicationFactory.create( this.igniteSettings ) );

        config.setGridLogger( LoggerConfig.create() );

        config.setMetricsLogFrequency( igniteSettings.metrics_log_frequency() );

        config.setClassLoader( ClassLoaderFactory.create( this.bundleContext ) );

        return config;
    }

    private AddressResolver getAddressResolver()
    {
        final String discoveryTcpLocalAddress = clusterConfig.networkHost();
        final String publishAddress = clusterConfig.networkPublishHost();
        if ( isEmpty( publishAddress ) || isEmpty( discoveryTcpLocalAddress ) )
        {
            return null;
        }

        final Map<String, String> addressMapping = new HashMap<>();
        addressMapping.put( discoveryTcpLocalAddress, publishAddress );

        LOG.info( "Ignite address mapping " + discoveryTcpLocalAddress + " -> " + publishAddress );
        try
        {
            return new BasicAddressResolver( addressMapping );
        }
        catch ( UnknownHostException e )
        {
            throw new RuntimeException( "Error creating Ignite AddressResolver", e );
        }
    }

    private String resolveIgniteHome()
    {
        if ( Strings.isNullOrEmpty( igniteSettings.home() ) )
        {
            return HomeDir.get().toFile().getPath();
        }

        return new File( this.igniteSettings.home() ).getPath();
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
