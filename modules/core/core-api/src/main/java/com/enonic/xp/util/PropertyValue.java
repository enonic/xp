package com.enonic.xp.util;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public final class PropertyValue
{
    private final Object value;

    private PropertyValue( final Object value )
    {
        this.value = Objects.requireNonNull( value );
    }

    public Optional<PropertyValue> optional( final String propertyName )
    {
        return whenMapOrElse( m -> Optional.ofNullable( m.get( propertyName ) ), Optional::empty );
    }

    public PropertyValue property( final String propertyName )
    {
        return whenMapOrElse( m -> m.get( propertyName ), () -> {
            throw new NoSuchElementException();
        } );
    }

    public Set<Map.Entry<String, PropertyValue>> getProperties()
    {
        return whenMapOrElse( Map::entrySet, ImmutableSet::of );
    }

    public List<PropertyValue> asList()
    {
        return whenListOrElse( Function.identity(), () -> ImmutableList.of( this ) );
    }

    public String asString()
    {
        return switch ( value )
        {
            case Long l -> Long.toString( l );
            case Double d -> Double.toString( d );
            case Boolean b -> Boolean.toString( b );
            case String s -> s;
            default -> throw new IllegalStateException();
        };
    }

    public double asDouble()
    {
        return switch ( value )
        {
            case Long l -> (double) l;
            case Double d -> d;
            case String s -> Double.parseDouble( s );
            default -> throw new IllegalStateException();
        };
    }

    public int asInteger()
    {
        return switch ( value )
        {
            case Long l -> Math.toIntExact( l );
            case Double d -> Math.toIntExact( d.longValue() );
            case String s -> Integer.parseInt( s );
            default -> throw new IllegalStateException();
        };
    }

    public long asLong()
    {
        return switch ( value )
        {
            case Long l -> l;
            case Double d -> d.longValue();
            case String s -> Long.parseLong( s );
            default -> throw new IllegalStateException();
        };
    }

    public boolean asBoolean()
    {
        return switch ( value )
        {
            case Boolean b -> b;
            default -> throw new IllegalStateException();
        };
    }

    public Type getType()
    {
        return switch ( value )
        {
            case String s -> Type.STRING;
            case Long l -> Type.NUMBER;
            case Double d -> Type.NUMBER;
            case Boolean b -> Type.BOOLEAN;
            case List<?> l -> Type.LIST;
            case Map<?, ?> m -> Type.OBJECT;
            default -> throw new AssertionError( value );
        };
    }

    @SuppressWarnings("unchecked")
    private <T> T whenMapOrElse( final Function<Map<String, PropertyValue>, T> then, final Supplier<T> orElse )
    {
        return value instanceof Map ? then.apply( (Map<String, PropertyValue>) value ) : orElse.get();
    }

    @SuppressWarnings("unchecked")
    private <T> T whenListOrElse( final Function<List<PropertyValue>, T> then, final Supplier<T> orElse )
    {
        return value instanceof Map ? then.apply( (List<PropertyValue>) value ) : orElse.get();
    }

    public static PropertyValue longValue( long value )
    {
        return new PropertyValue( value );
    }

    public static PropertyValue doubleValue( double value )
    {
        return new PropertyValue( value );
    }

    public static PropertyValue stringValue( String value )
    {
        return new PropertyValue( value );
    }

    public static PropertyValue booleanValue( boolean value )
    {
        return new PropertyValue( value );
    }

    public static PropertyValue listValue( List<PropertyValue> value )
    {
        return new PropertyValue( ImmutableList.copyOf( value ) );
    }

    public static PropertyValue objectValue( Map<String, PropertyValue> value )
    {
        return new PropertyValue( ImmutableMap.copyOf( value ) );
    }

    public enum Type
    {
        NUMBER, STRING, BOOLEAN, LIST, OBJECT
    }

    @Override
    public boolean equals( final Object o )
    {
        return o instanceof final PropertyValue that && Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( value );
    }
}