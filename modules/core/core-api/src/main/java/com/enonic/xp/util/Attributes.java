package com.enonic.xp.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public final class Attributes
{
    private final ImmutableMap<String, PropertyValue> list;

    private Attributes( final ImmutableMap<String, PropertyValue> list )
    {
        this.list = list;
    }

    public List<PropertyValue> list()
    {
        return list.values().asList();
    }

    public PropertyValue get( final String key )
    {
        return list.get( key );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableMap.Builder<String, PropertyValue> list = ImmutableMap.builder();

        public Builder add( final String key, final Map<String, PropertyValue> value )
        {
            ImmutableMap.Builder<String, PropertyValue> builder = ImmutableMap.builder();
            builder.putAll( value );
            builder.put( "_key", PropertyValue.stringValue( key ) );
            list.put( key, PropertyValue.objectValue( builder.build() ) );
            return this;
        }

        public Builder addAll( final Iterable<PropertyValue> values )
        {
            for ( PropertyValue value : values )
            {
                list.put( value.property( "_key" ).asString(), value );
            }
            return this;
        }

        public Attributes build()
        {
            return new Attributes( list.build() );
        }
    }
}
