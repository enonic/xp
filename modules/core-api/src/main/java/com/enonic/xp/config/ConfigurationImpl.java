package com.enonic.xp.config;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.convert.Converters;

final class ConfigurationImpl
    implements Configuration
{
    private final ImmutableMap<String, String> map;

    private ConfigurationImpl( final ImmutableMap<String, String> map )
    {
        this.map = map;
    }

    @Override
    public String get( final String key )
    {
        return this.map.get( key );
    }

    @Override
    public String getOrDefault( final String key, final String defValue )
    {
        final String value = get( key );
        return value != null ? value : defValue;
    }

    @Override
    public <T> T get( final String key, final Class<T> type )
    {
        final String value = get( key );
        return value != null ? Converters.convertOrNull( value, type ) : null;
    }

    @Override
    public <T> T getOrDefault( final String key, final Class<T> type, final T defValue )
    {
        final T value = get( key, type );
        return value != null ? value : defValue;
    }

    @Override
    public boolean exists( final String key )
    {
        return this.map.containsKey( key );
    }

    @Override
    public Configuration subConfig( final String prefix )
    {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for ( final Map.Entry<String, String> entry : this.map.entrySet() )
        {
            final String key = entry.getKey();
            if ( key.startsWith( prefix ) )
            {
                builder.put( key.substring( prefix.length() ), entry.getValue() );
            }
        }

        return new ConfigurationImpl( builder.build() );
    }

    public Map<String, String> asMap()
    {
        return this.map;
    }

    @Override
    public boolean equals( final Object other )
    {
        return ( other instanceof Configuration ) && equals( (Configuration) other );
    }

    private boolean equals( final Configuration other )
    {
        return this.map.equals( other.asMap() );
    }

    public static ConfigurationImpl create( final Map<String, String> map )
    {
        return new ConfigurationImpl( toImmutableMap( map ) );
    }

    private static ImmutableMap<String, String> toImmutableMap( final Map<String, String> map )
    {
        if ( map instanceof ImmutableMap )
        {
            return (ImmutableMap<String, String>) map;
        }

        return ImmutableMap.copyOf( map );
    }
}
