package com.enonic.xp.elasticsearch.impl.config;

import java.util.Map;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.osgi.framework.BundleContext;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

public final class NodeSettingsBuilder
{
    private static final String COMMON_NODE_NAME_OPTION = "node.name";

    private static final String COMMON_NETWORK_HOST_OPTION = "network.host";

    private static final String ES_NODE_LOCAL = "node.local";

    private final BundleContext context;

    private final Configuration elasticConfig;

    private final ClusterConfig clusterConfig;

    public NodeSettingsBuilder( final BundleContext context, final ClusterConfig clusterConfig )
    {
        this.context = context;
        this.elasticConfig = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            build();
        this.clusterConfig = clusterConfig;
    }

    public Settings buildSettings( final Map<String, String> map )
    {
        final Configuration config = buildConfig( map );
        return buildSettings( config );
    }

    private Configuration buildConfig( final Map<String, String> map )
    {
        final Configuration config = ConfigBuilder.create().
            addAll( this.elasticConfig ).
            addAll( map ).
            build();

        return new ConfigInterpolator().
            bundleContext( this.context ).
            interpolate( config );
    }

    private Settings buildSettings( final Configuration config )
    {
        return ImmutableSettings.settingsBuilder().
            classLoader( ImmutableSettings.class.getClassLoader() ).
            put( config.asMap() ).
            put( COMMON_NODE_NAME_OPTION, this.clusterConfig.name().toString() ).
            put( DiscoverySettingsFactory.create().
                discoveryConfig( clusterConfig.discoveryConfig() ).
                esConfig( config ).
                build().
                execute() ).
            put( COMMON_NETWORK_HOST_OPTION, this.clusterConfig.networkHost() ).
            put( ES_NODE_LOCAL, !this.clusterConfig.isEnabled() ).
            build();
    }

}
