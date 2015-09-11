package com.enonic.xp.config;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class ConfigurationTest
{
    @Test
    public void testEmpty()
    {
        final Configuration config = Configuration.create().build();

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
        final Configuration config = Configuration.create().
            add( "key1", "value1" ).
            add( "key2", "33" ).
            build();

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
    public void testAddAll()
    {
        final Map<String, String> source = Maps.newHashMap();
        source.put( "key1", "value1" );
        source.put( "key2", "value2" );

        final Configuration config = Configuration.create().
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
    public void testAddConfig()
    {
        final Configuration config1 = Configuration.create().
            add( "key1", "value1" ).
            add( "key2", "value2" ).
            build();

        final Configuration config2 = Configuration.create().
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
    public void testSubConfig()
    {
        final Configuration config1 = Configuration.create().
            add( "key1", "value1" ).
            add( "key2", "value2" ).
            add( "my.key3", "value3" ).
            build();

        final Configuration config2 = config1.subConfig( "my." );

        assertNotNull( config2 );
        assertNotSame( config1, config2 );
        assertEquals( false, config2.exists( "key1" ) );
        assertEquals( false, config2.exists( "key2" ) );
        assertEquals( true, config2.exists( "key3" ) );

        final Map<String, String> map = config2.asMap();
        assertEquals( 1, map.size() );
    }
}
