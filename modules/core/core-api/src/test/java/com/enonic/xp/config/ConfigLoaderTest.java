package com.enonic.xp.config;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest
{
    @Test
    public void loadConfig()
        throws Exception
    {
        final ConfigLoader loader = new ConfigLoader( getClass() );
        final Properties config = loader.load( "ConfigLoaderTest.properties" );

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "value2", config.get( "key2" ) );
    }

    @Test
    public void loadConfig_notFound()
        throws Exception
    {
        final ConfigLoader loader = new ConfigLoader( getClass() );
        assertThrows(IllegalArgumentException.class, () ->  loader.load( "unknown.properties" ) );
    }
}
