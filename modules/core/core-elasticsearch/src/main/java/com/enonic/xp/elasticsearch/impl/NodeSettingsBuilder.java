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

    public NodeSettingsBuilder( final BundleContext context, final ClusterConfig clusterConfig )
    {
        this.context = context;
        this.defaultConfig = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            build();
        this.clusterConfig = clusterConfig;
    }

    public Settings buildSettings( final Map<String, String> map )
    {
        final Configuration config = buildConfig( map );
        final Settings settings = buildSettings( config );
        return settings;
    }

    private Configuration buildConfig( final Map<String, String> map )
    {
        final Configuration source = ConfigBuilder.create().
            addAll( this.defaultConfig ).
            addAll( map ).
            add( "node.name", clusterConfig.name().toString() ).
            build();

        final ConfigInterpolator interpolator = new ConfigInterpolator();
        interpolator.bundleContext( this.context );
        return interpolator.interpolate( source );
    }

    private Settings buildSettings( final Configuration config )
    {
        return ImmutableSettings.settingsBuilder().
            classLoader( ImmutableSettings.class.getClassLoader() ).
            put( config.asMap() ).
            build();
    }
}
