package com.enonic.xp.config;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class ConfigurationTest
{
    @Test
    public void testEmpty()
    {
        final Map<String, String> source = Maps.newHashMap();
        final Configuration config = ConfigurationImpl.create( source );

        assertNotNull( config );
        assertEquals( false, config.exists( "key1" ) );
        assertNull( config.get( "key1" ) );
        assertEquals( "value1", config.getOrDefault( "key1", "value1" ) );
        assertNull( config.get( "key1", Integer.class ) );
        assertEquals( new Integer( 11 ), config.getOrDefault( "key1", Integer.class, 11 ) );

        final Map<String, String> map = config.asMap();
        assertTrue( map.isEmpty() );
    }

    @Test
    public void testConfig()
    {
        final Map<String, String> source = ImmutableMap.of( "key1", "value1", "key2", "33" );
        final Configuration config = ConfigurationImpl.create( source );

        assertNotNull( config );
        assertEquals( true, config.exists( "key1" ) );
        assertEquals( false, config.exists( "key3" ) );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "value1", config.getOrDefault( "key1", "value3" ) );
        assertEquals( "value3", config.getOrDefault( "key3", "value3" ) );
        assertNull( config.get( "key1", Integer.class ) );
        assertEquals( new Integer( 11 ), config.getOrDefault( "key1", Integer.class, 11 ) );
        assertEquals( new Integer( 33 ), config.getOrDefault( "key2", Integer.class, 11 ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 2, map.size() );
    }

    @Test
    public void testSubConfig()
    {
        final Map<String, String> source = ImmutableMap.of( "key1", "value1", "my.key2", "value2" );
        final Configuration config1 = ConfigurationImpl.create( source );

        final Configuration config2 = config1.subConfig( "my." );

        assertNotNull( config2 );
        assertNotSame( config1, config2 );
        assertEquals( false, config2.exists( "key1" ) );
        assertEquals( true, config2.exists( "key2" ) );

        final Map<String, String> map = config2.asMap();
        assertEquals( 1, map.size() );
    }

    @Test
    public void testEquals()
    {
        final Map<String, String> source1 = ImmutableMap.of( "key1", "value1", "key2", "value2" );
        final Configuration config1 = ConfigurationImpl.create( source1 );

        final Map<String, String> source2 = ImmutableMap.of( "key2", "value2", "key1", "value1" );
        final Configuration config2 = ConfigurationImpl.create( source2 );

        final Map<String, String> source3 = ImmutableMap.of( "key1", "value1" );
        final Configuration config3 = ConfigurationImpl.create( source3 );

        assertTrue( config1.equals( config2 ) );
        assertTrue( config2.equals( config1 ) );
        assertFalse( config3.equals( config1 ) );
        assertFalse( config3.equals( config2 ) );
    }
}
