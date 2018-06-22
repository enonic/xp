package com.enonic.xp.elasticsearch.impl;

import java.util.Map;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.osgi.framework.BundleContext;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

final class NodeSettingsBuilder
{
    private final BundleContext context;

    private final Configuration defaultConfig;

    private final ClusterConfig clusterConfig;

    NodeSettingsBuilder( final BundleContext context, final ClusterConfig clusterConfig )
    {
        this.context = context;
        this.defaultConfig = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            build();
        this.clusterConfig = clusterConfig;
    }

    Settings buildSettings( final Map<String, String> map )
    {
        final Configuration config = buildConfig( map );
        return buildSettings( config );
    }

    private Configuration buildConfig( final Map<String, String> map )
    {
        final Configuration config = ConfigBuilder.create().
            addAll( this.defaultConfig ).
            addAll( map ).
            build();

        final ConfigInterpolator interpolator = new ConfigInterpolator();
        interpolator.bundleContext( this.context );
        final Configuration mergedConfig = ClusterConfigMerger.merge( this.clusterConfig, config );
        return interpolator.interpolate( mergedConfig );
    }

    private Settings buildSettings( final Configuration config )
    {
        return ImmutableSettings.settingsBuilder().
            classLoader( ImmutableSettings.class.getClassLoader() ).
            put( "network.publish_host", this.clusterConfig.networkPublishHost() ).
            put( "network.host", this.clusterConfig.networkHost() ).
            put( config.asMap() ).
            put( "node.local", !this.clusterConfig.isEnabled() ).
            build();
    }
}
