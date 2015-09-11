package com.enonic.xp.config;

import java.io.FileNotFoundException;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationLoaderTest
{
    @Test
    public void loadConfig()
        throws Exception
    {
        final ConfigurationLoader loader = new ConfigurationLoader( getClass() );
        final Configuration config = loader.load( "ConfigurationLoaderTest.properties" );

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "value2", config.get( "key2" ) );
        assertEquals( "value3", config.get( "key3" ) );
        assertEquals( "value4", config.get( "key4" ) );
    }

    @Test(expected = FileNotFoundException.class)
    public void loadConfig_notFound()
        throws Exception
    {
        final ConfigurationLoader loader = new ConfigurationLoader( getClass() );
        loader.load( "unknown.properties" );
    }
}
