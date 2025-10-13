package com.enonic.xp.node;

import java.util.List;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.util.GenericValue;

public final class Attributes
{
    public static final String KEY_PROPERTY = "_key";

    private final ImmutableMap<String, GenericValue> attrs;

    private Attributes( final ImmutableMap<String, GenericValue> list )
    {
        this.attrs = list;
    }

    public List<GenericValue> list()
    {
        return attrs.values().asList();
    }

    public GenericValue get( final String key )
    {
        return attrs.get( key );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableMap.Builder<String, GenericValue> builder = ImmutableMap.builder();

        public AttributeBuilder attribute( final String key )
        {
            return new AttributeBuilder( this, key );
        }

        public Builder addAll( final Iterable<GenericValue> values )
        {
            for ( GenericValue value : values )
            {
                builder.put( value.property( KEY_PROPERTY ).asString(), value );
            }
            return this;
        }

        public Attributes build()
        {
            return new Attributes( builder.build() );
        }

        public Attributes buildKeepingLast()
        {
            return new Attributes( builder.buildKeepingLast() );
        }
    }

    public static final class AttributeBuilder
    {
        private final GenericValue.ObjectBuilder obj = GenericValue.object();

        private final Attributes.Builder attributesBuilder;

        private final String key;

        private AttributeBuilder( final Attributes.Builder attributesBuilder, final String key )
        {
            this.attributesBuilder = attributesBuilder;
            this.key = key;
            obj.put( KEY_PROPERTY, key );
        }

        public AttributeBuilder put( final String key, final GenericValue value )
        {
            obj.put( key, value );
            return this;
        }

        public AttributeBuilder put( final String key, final String value )
        {
            obj.put( key, value );
            return this;
        }

        public AttributeBuilder putArray( final String key, final List<String> value )
        {
            obj.put( key, GenericValue.stringList( value ) );
            return this;
        }

        public Attributes.Builder end()
        {
            attributesBuilder.builder.put( key, obj.build() );
            return attributesBuilder;
        }
    }
}
