package com.enonic.xp.elasticsearch.impl;

import java.io.File;
import java.util.Map;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.osgi.framework.BundleContext;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;
import com.enonic.xp.home.HomeDir;

final class NodeSettingsBuilder
{
    private final BundleContext context;

    private final Configuration defaultConfig;

    public NodeSettingsBuilder( final BundleContext context )
    {
        this.context = context;
        this.defaultConfig = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            build();
    }

    public Settings buildSettings( final Map<String, String> map )
    {
        final Configuration config = buildConfig( map );

        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
        settings.classLoader( ImmutableSettings.class.getClassLoader() );

        buildSettings( settings, config );
        return settings.build();
    }

    private Configuration buildConfig( final Map<String, String> map )
    {
        final Configuration source = ConfigBuilder.create().
            addAll( this.defaultConfig ).
            addAll( map ).
            build();

        final ConfigInterpolator interpolator = new ConfigInterpolator();
        interpolator.bundleContext( this.context );
        return interpolator.interpolate( source );
    }

    private void buildSettings( final ImmutableSettings.Builder settings, final Configuration config )
    {
        // TODO: Use config object to get values

        settings.put( "name", "local-node" );
        settings.put( "client", "false" );
        settings.put( "data", "true" );
        settings.put( "local", "true" );
        settings.put( "http.enabled", "true" );
        settings.put( "cluster.name", "mycluster" );
        settings.put( "network.host", "127.0.0.1" );
        settings.put( "discovery.zen.ping.multicast.enabled", "false" );
        settings.put( "cluster.routing.allocation.disk.threshold_enabled", "false" );

        final HomeDir xpHome = HomeDir.get();
        final File indexPath = new File( xpHome.toFile(), "repo/index" );
        settings.put( "path", indexPath.getAbsolutePath() );
        settings.put( "path.data", new File( indexPath, "data" ).getAbsolutePath() );
        settings.put( "path.work", new File( indexPath, "work" ).getAbsolutePath() );
        settings.put( "path.conf", new File( indexPath, "conf" ).getAbsolutePath() );
        settings.put( "path.logs", new File( indexPath, "logs" ).getAbsolutePath() );
        settings.put( "path.plugins", new File( indexPath, "plugins" ).getAbsolutePath() );
    }
}
