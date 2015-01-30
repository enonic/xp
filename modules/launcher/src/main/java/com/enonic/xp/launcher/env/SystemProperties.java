package com.enonic.xp.launcher.env;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

public final class SystemProperties
    extends HashMap<String, String>
{
    private final static String ENV_PREFIX = "env.";

    public String getEnv( final String key )
    {
        return get( ENV_PREFIX + key );
    }

    public String putEnv( final String key, final String value )
    {
        return put( ENV_PREFIX + key, value );
    }

    public void putAllEnv( final Map<String, String> map )
    {
        for ( final Map.Entry<String, String> entry : map.entrySet() )
        {
            putEnv( entry.getKey(), entry.getValue() );
        }
    }

    public static SystemProperties getDefault()
    {
        final SystemProperties properties = new SystemProperties();
        properties.putAll( Maps.fromProperties( System.getProperties() ) );
        properties.putAllEnv( System.getenv() );
        return properties;
    }
}
