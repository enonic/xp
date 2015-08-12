package com.enonic.xp.form.inputtype;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.google.common.annotations.Beta;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.convert.Converters;

@Beta
public final class InputTypeConfig
{
    private final Multimap<String, String> map;

    private InputTypeConfig( final Builder builder )
    {
        this.map = builder.map;
    }

    public String getValue( final String name )
    {
        final Collection<String> values = getValues( name );
        return values.isEmpty() ? null : values.iterator().next();
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

    public Collection<String> getValues( final String name )
    {
        final Collection<String> values = this.map.get( name );
        return values != null ? values : Collections.emptyList();
    }

    public boolean hasValue( final String name, final String value )
    {
        return getValues( name ).contains( value );
    }

    public Map<String, Collection<String>> asMap()
    {
        return this.map.asMap();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof InputTypeConfig ) && ( (InputTypeConfig) o ).map.equals( this.map );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Multimap<String, String> map;

        private Builder()
        {
            this.map = HashMultimap.create();
        }

        public Builder config( final InputTypeConfig config )
        {
            if ( config != null )
            {
                this.map.putAll( config.map );
            }

            return this;
        }

        public Builder property( final String name, final String... values )
        {
            for ( final String value : values )
            {
                this.map.put( name.trim(), value.trim() );
            }

            return this;
        }

        public InputTypeConfig build()
        {
            return new InputTypeConfig( this );
        }
    }
}
