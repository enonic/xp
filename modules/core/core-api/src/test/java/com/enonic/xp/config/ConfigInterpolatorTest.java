package com.enonic.xp.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigInterpolatorTest
{
    private Configuration newConfig()
    {
        return ConfigBuilder.create().
            add( "key10", "${key1} ${key2} ${key3} ${key4} ${key5} ${key6} ${key7}" ).
            add( "key1", "value1" ).
            add( "key2", "other ${key1}" ).
            add( "key3", "unknown ${other}" ).
            add( "key4", "env ${env.MYENV}" ).
            add( "key5", "${key2} ${key4}" ).
            add( "key6", "${systemProp}" ).
            add( "key7", "${bundleProp}" ).
            build();
    }

    private Map<String, String> newEnvironment()
    {
        final Map<String, String> map = new HashMap<>();
        map.put( "MYENV", "fromEnv" );
        return map;
    }

    private Properties newSystemProperties()
    {
        final Properties props = new Properties();
        props.put( "systemProp", "fromSystem" );
        return props;
    }

    private BundleContext newBundleContext()
    {
        final BundleContext bundleContext = Mockito.mock( BundleContext.class );
        Mockito.when( bundleContext.getProperty( "bundleProp" ) ).thenReturn( "fromBundle" );
        return bundleContext;
    }

    @Test
    void empty()
    {
        final ConfigInterpolator interpolator = new ConfigInterpolator();
        final Configuration config = interpolator.interpolate( newConfig() );

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "other value1", config.get( "key2" ) );
        assertEquals( "unknown ${other}", config.get( "key3" ) );
        assertEquals( "env ${env.MYENV}", config.get( "key4" ) );
        assertEquals( "other value1 env ${env.MYENV}", config.get( "key5" ) );
        assertEquals( "${systemProp}", config.get( "key6" ) );
        assertEquals( "${bundleProp}", config.get( "key7" ) );
    }

    @Test
    void environment()
    {
        final ConfigInterpolator interpolator = new ConfigInterpolator();
        interpolator.environment( newEnvironment() );
        final Configuration config = interpolator.interpolate( newConfig() );

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "other value1", config.get( "key2" ) );
        assertEquals( "unknown ${other}", config.get( "key3" ) );
        assertEquals( "env fromEnv", config.get( "key4" ) );
        assertEquals( "other value1 env fromEnv", config.get( "key5" ) );
        assertEquals( "${systemProp}", config.get( "key6" ) );
        assertEquals( "${bundleProp}", config.get( "key7" ) );
        assertEquals( "value1 other value1 unknown ${other} env fromEnv other value1 env fromEnv ${systemProp} ${bundleProp}",
                      config.get( "key10" ) );
    }

    @Test
    void systemProperties()
    {
        final ConfigInterpolator interpolator = new ConfigInterpolator();
        interpolator.systemProperties( newSystemProperties() );
        final Configuration config = interpolator.interpolate( newConfig() );

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "other value1", config.get( "key2" ) );
        assertEquals( "unknown ${other}", config.get( "key3" ) );
        assertEquals( "env ${env.MYENV}", config.get( "key4" ) );
        assertEquals( "other value1 env ${env.MYENV}", config.get( "key5" ) );
        assertEquals( "fromSystem", config.get( "key6" ) );
        assertEquals( "${bundleProp}", config.get( "key7" ) );
    }

    @Test
    void bundleContext()
    {
        final ConfigInterpolator interpolator = new ConfigInterpolator();
        interpolator.bundleContext( newBundleContext() );
        final Configuration config = interpolator.interpolate( newConfig() );

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "other value1", config.get( "key2" ) );
        assertEquals( "unknown ${other}", config.get( "key3" ) );
        assertEquals( "env ${env.MYENV}", config.get( "key4" ) );
        assertEquals( "other value1 env ${env.MYENV}", config.get( "key5" ) );
        assertEquals( "${systemProp}", config.get( "key6" ) );
        assertEquals( "fromBundle", config.get( "key7" ) );
    }
}

