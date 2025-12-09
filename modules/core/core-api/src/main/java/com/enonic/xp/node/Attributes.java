package com.enonic.xp.node;

import java.util.Map;
import java.util.Objects;
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

    @Override
    public boolean equals( final Object o )
    {
        return o instanceof final Attributes that && Objects.equals( attrs, that.attrs );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( attrs );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableMap.Builder<String, GenericValue> builder = ImmutableMap.builder();

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
}
