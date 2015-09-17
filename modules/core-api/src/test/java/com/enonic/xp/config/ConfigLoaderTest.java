package com.enonic.xp.config;

import java.util.Properties;

import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test(expected = IllegalArgumentException.class)
    public void loadConfig_notFound()
        throws Exception
    {
        final ConfigLoader loader = new ConfigLoader( getClass() );
        loader.load( "unknown.properties" );
    }
}
