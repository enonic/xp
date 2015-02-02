package com.enonic.xp.launcher.config;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.xp.launcher.SharedConstants;
import com.enonic.xp.launcher.env.Environment;

public final class ConfigLoader
    implements SharedConstants
{
    private final static String CONFIG_FILE = "system.properties";

    private final static String DEFAULT_CONFIG = "/META-INF/config/system.properties";

    private final Environment env;

    public ConfigLoader( final Environment env )
    {
        this.env = env;
    }

    public ConfigProperties load()
        throws Exception
    {
        final ConfigProperties props = new ConfigProperties();
        props.putAll( loadDefaultProperties() );
        props.putAll( loadFileProperties() );
        props.putAll( this.env.getAsMap() );
        return props;
    }

    private Map<String, String> loadDefaultProperties()
        throws Exception
    {
        final URL url = getClass().getResource( DEFAULT_CONFIG );
        Preconditions.checkNotNull( url, "Could not find " + DEFAULT_CONFIG );
        return loadProperties( Resources.asCharSource( url, Charsets.UTF_8 ) );
    }

    private Map<String, String> loadFileProperties()
        throws Exception
    {
        final File configDir = new File( this.env.getHomeDir(), "config" );
        final File file = new File( configDir, CONFIG_FILE );
        if ( !file.isFile() )
        {
            return Maps.newHashMap();
        }

        return loadProperties( Files.asCharSource( file, Charsets.UTF_8 ) );
    }

    private Map<String, String> loadProperties( final CharSource source )
        throws Exception
    {
        final Properties props = new Properties();
        props.load( source.openStream() );
        return Maps.fromProperties( props );
    }
}
