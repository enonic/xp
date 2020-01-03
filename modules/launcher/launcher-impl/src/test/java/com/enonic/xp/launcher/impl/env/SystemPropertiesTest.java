package com.enonic.xp.launcher.impl.env;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SystemPropertiesTest
{
    @Test
    public void testEnv()
    {
        final SystemProperties props = new SystemProperties();
        assertNull( props.getEnv( "MY_ENV" ) );

        assertNull( props.putEnv( "MY_ENV", "myvalue" ) );
        assertEquals( "myvalue", props.getEnv( "MY_ENV" ) );

        assertEquals( "myvalue", props.putEnv( "MY_ENV", "othervalue" ) );
        assertEquals( "othervalue", props.getEnv( "MY_ENV" ) );
        assertEquals( "othervalue", props.get( "env.MY_ENV" ) );
    }

    @Test
    public void testPutAllEnv()
    {
        final SystemProperties props = new SystemProperties();
        assertNull( props.getEnv( "MY_ENV" ) );

        final Map<String, String> env = Map.of( "MY_ENV", "myvalue" );
        props.putAllEnv( env );

        assertEquals( "myvalue", props.getEnv( "MY_ENV" ) );
        assertEquals( "myvalue", props.get( "env.MY_ENV" ) );
    }

    @Test
    public void testDefault()
    {
        final SystemProperties props = SystemProperties.getDefault();

        final String propKey = System.getProperties().keySet().iterator().next().toString();
        final String propValue = System.getProperties().getProperty( propKey );
        assertEquals( propValue, props.get( propKey ) );

        final String envKey = System.getenv().keySet().iterator().next();
        final String envValue = System.getenv( envKey );
        assertEquals( envValue, props.getEnv( envKey ) );
    }
}
