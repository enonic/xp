package com.enonic.xp.node;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.util.GenericValue;

public final class Attributes
{
    private final ImmutableMap<String, GenericValue> attrs;

    private Attributes( final ImmutableMap<String, GenericValue> list )
    {
        this.attrs = list;
    }

    public Set<Map.Entry<String, GenericValue>> entrySet()
    {
        return attrs.entrySet();
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

        public Builder attribute( final String key, final GenericValue value )
        {
            builder.put( key, value );
            return this;
        }

        public Builder addAll( final Iterable<Map.Entry<String, GenericValue>> values )
        {
            for ( var value : values )
            {
                builder.put( value.getKey(), value.getValue() );
            }
            return this;
        }

        public Attributes build()
        {
            return new Attributes( builder.buildOrThrow() );
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

        public Attributes.Builder end()
        {
            attributesBuilder.builder.put( key, obj.build() );
            return attributesBuilder;
        }
    }
}
