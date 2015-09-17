package com.enonic.xp.config;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

public final class ConfigBuilder
{
    private final Map<String, String> map;

    private ConfigBuilder()
    {
        this.map = Maps.newHashMap();
    }

    public ConfigBuilder add( final String key, final Object value )
    {
        if ( value != null )
        {
            this.map.put( key.trim(), value.toString().trim() );
        }

        return this;
    }

    private void add( final Map.Entry<String, ?> entry )
    {
        add( entry.getKey(), entry.getValue() );
    }

    public ConfigBuilder addAll( final Configuration config )
    {
        return addAll( config.asMap() );
    }

    public ConfigBuilder addAll( final Map<String, ?> map )
    {
        map.entrySet().forEach( this::add );
        return this;
    }

    public ConfigBuilder addAll( final Dictionary<String, ?> map )
    {
        for ( final String key : Collections.list( map.keys() ) )
        {
            add( key, map.get( key ) );
        }

        return this;
    }

    public ConfigBuilder addAll( final Properties map )
    {
        return addAll( Maps.fromProperties( map ) );
    }

    public ConfigBuilder load( final Class context, final String name )
    {
        return addAll( new ConfigLoader( context ).load( name ) );
    }

    public Configuration build()
    {
        return ConfigurationImpl.create( this.map );
    }

    public static ConfigBuilder create()
    {
        return new ConfigBuilder();
    }
}
