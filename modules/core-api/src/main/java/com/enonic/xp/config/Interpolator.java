package com.enonic.xp.config;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;

public final class Interpolator
{
    private final static String ENV_PREFIX = "env.";

    private Map<String, String> environment;

    private Properties systemProperties;

    private BundleContext bundleContext;

    public Interpolator environment( final Map<String, String> map )
    {
        this.environment = map;
        return this;
    }

    public Interpolator systemProperties( final Properties properties )
    {
        this.systemProperties = properties;
        return this;
    }

    public Interpolator bundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
        return this;
    }

    public Configuration interpolate( final Configuration source )
    {
        final Map<String, String> target = Maps.newHashMap();
        target.putAll( source.asMap() );

        doInterpolate( target );

        return Configuration.create().
            addAll( target ).
            build();
    }

    private String lookupValue( final String key, final Map<String, String> map )
    {
        final String value1 = map.get( key );
        if ( value1 != null )
        {
            return value1;
        }

        final String value2 = getFromBundleContext( key );
        if ( value2 != null )
        {
            return value2;
        }

        final String value3 = getFromSystemProperties( key );
        if ( value3 != null )
        {
            return value3;
        }

        return getFromEnvironment( key );
    }

    private String getFromBundleContext( final String key )
    {
        return this.bundleContext != null ? this.bundleContext.getProperty( key ) : null;
    }

    private String getFromSystemProperties( final String key )
    {
        return this.systemProperties != null ? this.systemProperties.getProperty( key ) : null;
    }

    private String getFromEnvironment( final String key )
    {
        if ( !key.startsWith( ENV_PREFIX ) )
        {
            return null;
        }

        final String envKey = key.substring( ENV_PREFIX.length() );
        return this.environment != null ? this.environment.get( envKey ) : null;
    }

    private void doInterpolate( final Map<String, String> map )
    {
        final StrLookup lookup = new StrLookup()
        {
            @Override
            public String lookup( final String key )
            {
                return lookupValue( key, map );
            }
        };

        final StrSubstitutor substitutor = new StrSubstitutor( lookup );
        for ( final Map.Entry<String, String> entry : map.entrySet() )
        {
            final String key = entry.getKey();
            final String value = substitutor.replace( entry.getValue() );
            map.put( key, StringUtils.trim( value ) );
        }
    }
}
