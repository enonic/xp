package com.enonic.wem.launcher.util;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import junit.framework.Assert;

public class SystemPropertiesTest
{
    @Test
    public void testEnv()
    {
        final SystemProperties props = new SystemProperties();
        Assert.assertNull( props.getEnv( "MYVAR" ) );

        props.putEnv( "MYVAR", "test" );
        Assert.assertEquals( "test", props.getEnv( "MYVAR" ) );
        Assert.assertEquals( "test", props.get( "env.MYVAR" ) );
    }

    @Test
    public void testEnvMap()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put( "MYVAR", "test" );
        map.put( "HOME", "myhome" );

        final SystemProperties props = new SystemProperties();
        props.putAllEnv( map );

        Assert.assertEquals( "test", props.getEnv( "MYVAR" ) );
        Assert.assertEquals( "test", props.get( "env.MYVAR" ) );
        Assert.assertEquals( "myhome", props.getEnv( "HOME" ) );
        Assert.assertEquals( "myhome", props.get( "env.HOME" ) );
    }

    @Test
    public void testDefault()
    {
        final SystemProperties props = SystemProperties.getDefault();
        Assert.assertNotNull( props );

        final int totalSize = System.getenv().size() + System.getProperties().size();
        Assert.assertEquals( totalSize, props.size() );
    }
}
