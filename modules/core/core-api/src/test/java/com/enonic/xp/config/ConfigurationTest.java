package com.enonic.xp.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationTest
{
    @Test
    void testEmpty()
    {
        final Map<String, String> source = new HashMap<>();
        final Configuration config = ConfigurationImpl.create( source );

        assertNotNull( config );
        assertFalse( config.exists( "key1" ) );
        assertNull( config.get( "key1" ) );
        assertEquals( "value1", config.getOrDefault( "key1", "value1" ) );
        assertNull( config.get( "key1", Integer.class ) );
        assertEquals( 11, config.getOrDefault( "key1", Integer.class, 11 ) );

        final Map<String, String> map = config.asMap();
        assertTrue( map.isEmpty() );
    }

    @Test
    void testConfig()
    {
        final Map<String, String> source = Map.of( "key1", "value1", "key2", "33" );
        final Configuration config = ConfigurationImpl.create( source );

        assertNotNull( config );
        assertTrue( config.exists( "key1" ) );
        assertFalse( config.exists( "key3" ) );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "value1", config.getOrDefault( "key1", "value3" ) );
        assertEquals( "value3", config.getOrDefault( "key3", "value3" ) );
        assertNull( config.get( "key1", Integer.class ) );
        assertEquals( 11, config.getOrDefault( "key1", Integer.class, 11 ) );
        assertEquals( 33, config.getOrDefault( "key2", Integer.class, 11 ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 2, map.size() );
    }

    @Test
    void testSubConfig()
    {
        final Map<String, String> source = Map.of( "key1", "value1", "my.key2", "value2" );
        final Configuration config1 = ConfigurationImpl.create( source );

        final Configuration config2 = config1.subConfig( "my." );

        assertNotNull( config2 );
        assertNotSame( config1, config2 );
        assertFalse( config2.exists( "key1" ) );
        assertTrue( config2.exists( "key2" ) );

        final Map<String, String> map = config2.asMap();
        assertEquals( 1, map.size() );
    }

    @Test
    void testEquals()
    {
        final Map<String, String> source1 = Map.of( "key1", "value1", "key2", "value2" );
        final Configuration config1 = ConfigurationImpl.create( source1 );

        final Map<String, String> source2 = Map.of( "key2", "value2", "key1", "value1" );
        final Configuration config2 = ConfigurationImpl.create( source2 );

        final Map<String, String> source3 = Map.of( "key1", "value1" );
        final Configuration config3 = ConfigurationImpl.create( source3 );

        assertEquals( config1, config2 );
        assertEquals( config2, config1 );
        assertNotEquals( config3, config1 );
        assertNotEquals( config3, config2 );
    }
}
