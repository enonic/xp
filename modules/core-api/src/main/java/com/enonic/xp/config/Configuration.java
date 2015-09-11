package com.enonic.xp.config;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.convert.Converters;

public final class Configuration
{
    private final ImmutableMap<String, String> map;

    private Configuration( final ImmutableMap<String, String> map )
    {
        this.map = map;
    }

    public String get( final String key )
    {
        return this.map.get( key );
    }

    public String getOrDefault( final String key, final String defValue )
    {
        final String value = get( key );
        return value != null ? value : defValue;
    }

    public <T> T get( final String key, final Class<T> type )
    {
        final String value = get( key );
        return value != null ? Converters.convertOrNull( value, type ) : null;
    }

    public <T> T getOrDefault( final String key, final Class<T> type, final T defValue )
    {
        final T value = get( key, type );
        return value != null ? value : defValue;
    }

    public boolean exists( final String key )
    {
        return this.map.containsKey( key );
    }

    public Configuration subConfig( final String prefix )
    {
        final Builder builder = create();
        for ( final Map.Entry<String, String> entry : this.map.entrySet() )
        {
            final String key = entry.getKey();
            if ( key.startsWith( prefix ) )
            {
                builder.add( key.substring( prefix.length() ), entry.getValue() );
            }
        }

        return builder.build();
    }

    public Map<String, String> asMap()
    {
        return this.map;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private final Map<String, String> map;

        public Builder()
        {
            this.map = Maps.newHashMap();
        }

        public Builder add( final String key, final String value )
        {
            this.map.put( key, value );
            return this;
        }

        public Builder addAll( final Configuration config )
        {
            return addAll( config.map );
        }

        public Builder addAll( final Map<String, String> map )
        {
            this.map.putAll( map );
            return this;
        }

        public Configuration build()
        {
            return new Configuration( ImmutableMap.copyOf( this.map ) );
        }
    }
}
