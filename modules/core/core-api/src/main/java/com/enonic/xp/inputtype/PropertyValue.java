package com.enonic.xp.inputtype;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class PropertyValue
{
    private final Object value;

    private PropertyValue( final Object value )
    {
        this.value = Objects.requireNonNull( value );
    }

    @SuppressWarnings("unchecked")
    public Set<Map.Entry<String, PropertyValue>> getProperties()
    {
        return switch ( value )
        {
            case Map<?, ?> map -> ( (Map<String, PropertyValue>) map ).entrySet();
            default -> Collections.emptySet();
        };
    }

    @SuppressWarnings("unchecked")
    public List<PropertyValue> asList()
    {
        return switch ( value )
        {
            case List<?> list -> (List<PropertyValue>) list;
            default -> List.of( this );
        };
    }

    public String asString()
    {
        return switch ( value )
        {
            case Long l -> Long.toString( l );
            case Double d -> Double.toString( d );
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
}
