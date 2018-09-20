package com.enonic.xp.elasticsearch.impl.config;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.config.Configuration;
import com.enonic.xp.convert.Converters;

public class ConfigurationTestImpl
    implements Configuration
{
    private final Map<String, String> map = Maps.newHashMap();

    public void put( final String key, final String value )
    {
        this.map.put( key, value );
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
    public Configuration subConfig( final String prefix )
    {
        return null;
    }

    @Override
    public Map<String, String> asMap()
    {
        return this.map;
    }

    @Override
    public boolean exists( final String key )
    {
        return this.map.containsKey( key );
    }
}
