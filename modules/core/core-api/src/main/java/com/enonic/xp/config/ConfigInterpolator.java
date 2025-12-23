package com.enonic.xp.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import com.enonic.xp.core.internal.Interpolator;

public final class ConfigInterpolator
{
    private static final String ENV_PREFIX = "env.";

    private Map<String, String> environment;

    private Properties systemProperties;

    private final List<Function<String, String>> additionalLookups = new CopyOnWriteArrayList<>();

    public ConfigInterpolator()
    {
        this.environment = System.getenv();
        this.systemProperties = System.getProperties();
    }

    public ConfigInterpolator addLookup( final Function<String, String> lookup )
    {
        this.additionalLookups.add( lookup );
        return this;
    }

    ConfigInterpolator environment( final Map<String, String> map )
    {
        this.environment = map;
        return this;
    }

    ConfigInterpolator systemProperties( final Properties properties )
    {
        this.systemProperties = properties;
        return this;
    }

    public Configuration interpolate( final Configuration source )
    {
        final Map<String, String> target = new HashMap<>( source.asMap() );

        doInterpolate( target );

        return ConfigurationImpl.create( target );
    }

    private String lookupValue( final String key, final Map<String, String> map )
    {
        final String value1 = map.get( key );
        if ( value1 != null )
        {
            return value1;
        }

        for ( Function<String, String> additionalLookup : additionalLookups )
        {
            final String value = additionalLookup.apply( key );
            if ( value != null )
            {
                return value;
            }
        }

        final String value3 = this.systemProperties != null ? this.systemProperties.getProperty( key ) : null;
        if ( value3 != null )
        {
            return value3;
        }

        return getFromEnvironment( key );
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
        final Function<String, String> lookup = key -> lookupValue( key, map );
        for ( final Map.Entry<String, String> entry : map.entrySet() )
        {
            final String key = entry.getKey();
            final String value = Interpolator.classic().interpolate( entry.getValue(), lookup ).trim();
            map.put( key, value );
        }
    }
}
