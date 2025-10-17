package com.enonic.xp.util;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class Attributes
{
    private final ImmutableMap<String, PropertyValue> attrs;

    private Attributes( final ImmutableMap<String, PropertyValue> list )
    {
        this.attrs = list;
    }

    public List<PropertyValue> list()
    {
        return attrs.values().asList();
    }

    public PropertyValue get( final String key )
    {
        return attrs.get( key );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableMap.Builder<String, PropertyValue> builder = ImmutableMap.builder();

        public AttributeBuilder attribute( final String key )
        {
            return new AttributeBuilder( this, key );
        }

        public Builder addAll( final Iterable<PropertyValue> values )
        {
            for ( PropertyValue value : values )
            {
                builder.put( value.property( "_key" ).asString(), value );
            }
            return this;
        }

        public Attributes build()
        {
            return new Attributes( builder.build() );
        }
    }

    public static final class AttributeBuilder
    {
        private final PropertyValue.ObjectBuilder obj = PropertyValue.object();

        private final Attributes.Builder attributesBuilder;

        private final String key;

        private AttributeBuilder( final Attributes.Builder attributesBuilder, final String key )
        {
            this.attributesBuilder = attributesBuilder;
            this.key = key;
            obj.put( "_key", key );
        }

        public AttributeBuilder put( final String key, final String value )
        {
            obj.put( key, value );
            return this;
        }

        public AttributeBuilder putArray( final String key, final List<String> value )
        {
            obj.put( key, PropertyValue.listValue(
                value.stream().map( PropertyValue::stringValue ).collect( ImmutableList.toImmutableList() ) ) );
            return this;
        }

        public Attributes.Builder end()
        {
            attributesBuilder.builder.put( key, obj.build() );
            return attributesBuilder;
        }
    }
}
