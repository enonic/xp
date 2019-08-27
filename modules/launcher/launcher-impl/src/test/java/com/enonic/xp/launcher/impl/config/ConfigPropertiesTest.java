package com.enonic.xp.launcher.impl.config;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

public class ConfigPropertiesTest
{
    @Test
    public void testPut()
    {
        final ConfigProperties props = new ConfigProperties();
        props.put( "key1", "value1" );
        props.put( " key2 ", " value2 " );

        assertEquals( "value1", props.get( "key1" ) );
        assertEquals( " value2 ", props.get( " key2 " ) );
    }

    @Test
    public void testPutAll()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "key1", "value1" );
        map.put( " key2 ", " value2 " );

        final ConfigProperties props = new ConfigProperties();
        props.putAll( map );

        assertEquals( "value1", props.get( "key1" ) );
        assertEquals( " value2 ", props.get( " key2 " ) );
    }

    @Test
    public void testInterpolate()
    {
        final ConfigProperties props = new ConfigProperties();
        props.put( "key1", "value1" );
        props.put( "key2", "value2 ${key1}" );
        props.put( "key3", "${key1} value3 ${key2}" );

        props.interpolate();
        assertEquals( "value1", props.get( "key1" ) );
        assertEquals( "value2 value1", props.get( "key2" ) );
        assertEquals( "value1 value3 value2 value1", props.get( "key3" ) );
    }

    @Test
    public void testInterpolate_errorLoop()
    {
        final ConfigProperties props = new ConfigProperties();
        props.put( "key1", "value1 ${key1}" );
        assertEquals( 1, props.size() );

        assertThrows(IllegalArgumentException.class, () -> props.interpolate());
    }
}
