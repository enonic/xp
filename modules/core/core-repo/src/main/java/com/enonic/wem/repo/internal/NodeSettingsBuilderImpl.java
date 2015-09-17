package com.enonic.wem.repo.internal;

import java.io.File;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import com.enonic.xp.home.HomeDir;

final class NodeSettingsBuilderImpl
    implements NodeSettingsBuilder
{
    @Override
    public Settings buildSettings()
    {
        final ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
        builder.classLoader( ImmutableSettings.class.getClassLoader() );

        // TODO: Hardcoded configuration. Should be using OSGi config admin.
        builder.put( "name", "local-node" );
        builder.put( "client", "false" );
        builder.put( "data", "true" );
        builder.put( "local", "true" );
        builder.put( "http.enabled", "true" );
        builder.put( "cluster.name", "mycluster" );
        builder.put( "network.host", "127.0.0.1");
        builder.put( "discovery.zen.ping.multicast.enabled", "false" );
        builder.put( "cluster.routing.allocation.disk.threshold_enabled", "false" );

        final HomeDir xpHome = HomeDir.get();
        final File indexPath = new File( xpHome.toFile(), "repo/index" );
        builder.put( "path", indexPath.getAbsolutePath() );
        builder.put( "path.data", new File( indexPath, "data" ).getAbsolutePath() );
        builder.put( "path.work", new File( indexPath, "work" ).getAbsolutePath() );
        builder.put( "path.conf", new File( indexPath, "conf" ).getAbsolutePath() );
        builder.put( "path.logs", new File( indexPath, "logs" ).getAbsolutePath() );
        builder.put( "path.plugins", new File( indexPath, "plugins" ).getAbsolutePath() );

        return builder.build();
    }
}
