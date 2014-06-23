package com.enonic.wem.launcher.config;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import junit.framework.Assert;

public class ConfigPropertiesTest
{
    @Test
    public void testPut()
    {
        final ConfigProperties props = new ConfigProperties();
        props.put( "key1", "value1" );
        props.put( " key2 ", " value2 " );

        Assert.assertEquals( "value1", props.get( "key1" ) );
        Assert.assertEquals( "value2", props.get( "key2" ) );
    }

    @Test
    public void testPutAll()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "key1", "value1" );
        map.put( " key2 ", " value2 " );

        final ConfigProperties props = new ConfigProperties();
        props.putAll( map );

        Assert.assertEquals( "value1", props.get( "key1" ) );
        Assert.assertEquals( "value2", props.get( "key2" ) );
    }

    @Test
    public void testInterpolate()
    {
        final ConfigProperties props = new ConfigProperties();
        props.put( "key1", "value1" );
        props.put( "key2", "value2 ${key1}" );
        props.put( "key3", "${key1} value3 ${key2}" );

        props.interpolate();
        Assert.assertEquals( "value1", props.get( "key1" ) );
        Assert.assertEquals( "value2 value1", props.get( "key2" ) );
        Assert.assertEquals( "value1 value3 value2 value1", props.get( "key3" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInterpolate_errorLoop()
    {
        final ConfigProperties props = new ConfigProperties();
        props.put( "key1", "value1 ${key1}" );
        Assert.assertEquals( 1, props.size() );

        props.interpolate();
        Assert.fail( "Should throw exception" );
    }
}
