package com.enonic.xp.config;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;

import static org.junit.Assert.*;

public class ConfigInterpolatorTest
{
    private Configuration newConfig()
    {
        return ConfigBuilder.create().
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
        final Map<String, String> map = Maps.newHashMap();
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
    public void empty()
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
    public void environment()
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
    }

    @Test
    public void systemProperties()
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
    public void bundleContext()
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

