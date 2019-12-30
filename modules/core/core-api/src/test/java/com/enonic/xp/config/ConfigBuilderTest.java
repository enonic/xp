package com.enonic.xp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.Dictionaries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigBuilderTest
{
    @Test
    public void testEmpty()
    {
        final Configuration config = ConfigBuilder.create().build();

        assertNotNull( config );
        assertTrue( config.asMap().isEmpty() );
    }

    @Test
    public void testAdd()
    {
        final Configuration config = ConfigBuilder.create().
            add( "key1", "value1" ).
            add( "  key2  ", "  value2  " ).
            add( "key3", 11 ).
            add( "key4", null ).
            build();

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "value2", config.get( "key2" ) );
        assertEquals( "11", config.get( "key3" ) );
        assertEquals( 3, config.asMap().size() );
    }

    @Test
    public void testAddAll_map()
    {
        final Map<String, String> source = new HashMap<>();
        source.put( "key1", "value1" );
        source.put( "key2", "value2" );

        final Configuration config = ConfigBuilder.create().
            addAll( source ).
            add( "key3", "value3" ).
            build();

        assertNotNull( config );
        assertEquals( true, config.exists( "key1" ) );
        assertEquals( true, config.exists( "key2" ) );
        assertEquals( true, config.exists( "key3" ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    public void testAddAll_properties()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "value1" );
        properties.put( "key2", "value2" );

        final Configuration config = ConfigBuilder.create().
            addAll( properties ).
            add( "key3", "value3" ).
            build();

        assertNotNull( config );
        assertEquals( true, config.exists( "key1" ) );
        assertEquals( true, config.exists( "key2" ) );
        assertEquals( true, config.exists( "key3" ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    public void testAddAll_dictionary()
    {
        Map<String, String> dictionary = Map.of( "key1", "value1", "key2", "value2" );

        final Configuration config = ConfigBuilder.create().
            addAll( Dictionaries.copyOf( dictionary ) ).
            add( "key3", "value3" ).
            build();

        assertNotNull( config );
        assertEquals( true, config.exists( "key1" ) );
        assertEquals( true, config.exists( "key2" ) );
        assertEquals( true, config.exists( "key3" ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    public void testAddConfig()
    {
        final Configuration config1 = ConfigBuilder.create().
            add( "key1", "value1" ).
            add( "key2", "value2" ).
            build();

        final Configuration config2 = ConfigBuilder.create().
            addAll( config1 ).
            add( "key3", "value3" ).
            build();

        assertNotNull( config2 );
        assertEquals( true, config2.exists( "key1" ) );
        assertEquals( true, config2.exists( "key2" ) );
        assertEquals( true, config2.exists( "key3" ) );

        final Map<String, String> map = config2.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    public void testLoad()
    {
        final Configuration config1 = ConfigBuilder.create().
            load( getClass(), "ConfigLoaderTest.properties" ).
            add( "key3", "value3" ).
            build();

        final Configuration config2 = ConfigBuilder.create().
            addAll( config1 ).
            add( "key3", "value3" ).
            build();

        assertNotNull( config2 );
        assertEquals( true, config2.exists( "key1" ) );
        assertEquals( true, config2.exists( "key2" ) );
        assertEquals( true, config2.exists( "key3" ) );

        final Map<String, String> map = config2.asMap();
        assertEquals( 3, map.size() );
    }
}
