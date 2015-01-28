package com.enonic.wem.launcher.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import com.enonic.wem.launcher.SharedConstants;
import com.enonic.wem.launcher.env.Environment;

public final class ConfigLoader2
    implements SharedConstants
{
    private final static String CONFIG_FILE = "config/system.properties";

    private final static String DEFAULT_CONFIG = "system.properties";

    private final Environment env;

    public ConfigLoader2( final Environment env )
    {
        this.env = env;
    }

    public ConfigProperties load()
        throws IOException
    {
        final ConfigProperties props = new ConfigProperties();
        props.putAll( loadDefaultProperties() );
        props.putAll( loadFileProperties() );
        props.putAll( this.env.getAsMap() );
        return props;
    }

    private Map<String, String> loadDefaultProperties()
        throws IOException
    {
        final URL url = getClass().getResource( DEFAULT_CONFIG );
        Preconditions.checkNotNull( url, "Could not find " + DEFAULT_CONFIG + " file in classpath." );
        return loadProperties( Resources.asCharSource( url, Charsets.UTF_8 ) );
    }

    private Map<String, String> loadFileProperties()
        throws IOException
    {
        final File file = new File( this.env.getHomeDir(), CONFIG_FILE );
        if ( !file.isFile() )
        {
            return Maps.newHashMap();
        }

        return loadProperties( Files.asCharSource( file, Charsets.UTF_8 ) );
    }

    private Map<String, String> loadProperties( final CharSource source )
        throws IOException
    {
        final Properties props = new Properties();
        props.load( source.openStream() );
        return Maps.fromProperties( props );
    }
}
