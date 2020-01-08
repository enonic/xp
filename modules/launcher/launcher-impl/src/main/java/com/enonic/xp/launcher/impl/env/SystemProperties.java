package com.enonic.xp.launcher.impl.env;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        final Map<String, String> systemPropertiesMap =
            System.getProperties().entrySet().stream().collect( Collectors.toMap( e -> (String) e.getKey(), e -> (String) e.getValue() ) );
        properties.putAll( systemPropertiesMap );
        properties.putAllEnv( System.getenv() );
        return properties;
    }
}
