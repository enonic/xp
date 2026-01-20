package com.enonic.xp.launcher.impl.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import com.enonic.xp.launcher.impl.env.Environment;

public final class ConfigLoader
{
    private static final String CONFIG_FILE = "system.properties";

    private static final String DEFAULT_CONFIG = "/META-INF/config/system.properties";

    private final Environment env;

    public ConfigLoader( final Environment env )
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
        final InputStream stream = getClass().getResourceAsStream( DEFAULT_CONFIG );
        Objects.requireNonNull( stream, "Could not find " + DEFAULT_CONFIG );

        try (stream; Reader reader = new BufferedReader( new InputStreamReader( stream, StandardCharsets.UTF_8 ) ))
        {
            return loadProperties( reader );
        }
    }

    private Map<String, String> loadFileProperties()
        throws IOException
    {
        final Path file = this.env.getHomeDir().resolve( "config" ).resolve( CONFIG_FILE );
        if ( !Files.isRegularFile( file ) )
        {
            return new HashMap<>();
        }

        try (Reader reader = Files.newBufferedReader( file, StandardCharsets.UTF_8 ))
        {
            return loadProperties( reader );
        }
    }

    private Map<String, String> loadProperties( final Reader reader )
        throws IOException
    {
        final Properties props = new Properties();
        props.load( reader );

        Map<String, String> map = new HashMap<>();
        for ( String propertyName : props.stringPropertyNames() )
        {
            map.put( propertyName.trim(), props.getProperty( propertyName ).trim() );
        }
        return map;
    }
}
