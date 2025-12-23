package com.enonic.xp.server.internal.config;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

final class ConfigLoader
{
    private final ConfigInterpolator interpolator;

    ConfigLoader( final BundleContext context )
    {
        this.interpolator = new ConfigInterpolator().addLookup( context::getProperty );
    }

    Map<String, String> load( final File file )
        throws Exception
    {
        final Properties props = new Properties();
        try (FileReader reader = new FileReader( file, StandardCharsets.UTF_8 ))
        {
            props.load( reader );
        }

        final ConfigBuilder builder = ConfigBuilder.create();
        builder.addAll( props );

        final Configuration config = this.interpolator.interpolate( builder.build() );
        return config.asMap();
    }
}
