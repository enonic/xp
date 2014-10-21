package com.enonic.wem.core.elasticsearch.resource;

import java.io.File;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import com.enonic.wem.core.home.HomeDir;

public final class NodeSettingsBuilderImpl
    implements NodeSettingsBuilder
{
    @Override
    public Settings buildSettings()
    {
        final ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
        builder.classLoader( getClass().getClassLoader() );

        // TODO: Hardcoded configuration. Should be using OSGi config admin.
        builder.put( "name", "local" );
        builder.put( "node.client", "false" );
        builder.put( "node.data", "true" );
        builder.put( "http.enabled", "true" );
        builder.put( "cluster.name", "mycluster" );

        final File indexPath = new File( HomeDir.get().toFile(), "index" );
        builder.put( "path", indexPath.getAbsolutePath() );
        builder.put( "path.data", new File( indexPath, "data" ).getAbsolutePath() );
        builder.put( "path.work", new File( indexPath, "work" ).getAbsolutePath() );
        builder.put( "path.conf", new File( indexPath, "conf" ).getAbsolutePath() );
        builder.put( "path.logs", new File( indexPath, "logs" ).getAbsolutePath() );
        builder.put( "path.plugins", new File( indexPath, "plugins" ).getAbsolutePath() );

        return builder.build();
    }
}
