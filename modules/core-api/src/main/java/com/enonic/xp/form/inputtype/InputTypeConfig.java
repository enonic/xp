package com.enonic.xp.form.inputtype;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.convert.Converters;

@Beta
public final class InputTypeConfig
{
    private final LinkedHashMap<String, String> map;

    private InputTypeConfig( final Builder builder )
    {
        this.map = builder.map;
    }

    public String getValue( final String name )
    {
        return this.map.get( name );
    }

    public <T> T getValue( final String name, final Class<T> type )
    {
        return getValue( name, type, null );
    }

    public <T> T getValue( final String name, final Class<T> type, final T defValue )
    {
        final String value = getValue( name );
        if ( value == null )
        {
            return defValue;
        }

        final T converted = Converters.convert( value, type );
        return converted != null ? converted : defValue;
    }

    public boolean hasProperty( final String name )
    {
        return this.map.containsKey( name );
    }

    public Set<String> getValues( final String name )
    {
        final String value = getValue( name );
        return value != null ? Sets.newHashSet( Splitter.on( "," ).trimResults().split( value ) ) : Collections.emptySet();
    }

    public Map<String, String> asMap()
    {
        return Collections.unmodifiableMap( this.map );
    }

    public Map<String, String> toSubMap( final String prefix )
    {
        final LinkedHashMap<String, String> result = Maps.newLinkedHashMap();
        this.map.entrySet().stream().filter( e -> e.getKey().startsWith( prefix ) ).forEach(
            e -> result.put( e.getKey().substring( prefix.length() ), e.getValue() ) );

        return result;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final LinkedHashMap<String, String> map;

        private Builder()
        {
            this.map = Maps.newLinkedHashMap();
        }

        public Builder config( final InputTypeConfig config )
        {
            if ( config != null )
            {
                this.map.putAll( config.map );
            }

            return this;
        }

        public Builder property( final String name, final String value )
        {
            this.map.put( name.trim(), value.trim() );
            return this;
        }

        public Builder property( final String name, final Iterable<String> values )
        {
            return property( name, Joiner.on( "," ).skipNulls().join( values ) );
        }

        public InputTypeConfig build()
        {
            return new InputTypeConfig( this );
        }
    }
}
