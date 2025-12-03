package com.enonic.xp.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
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

public final class GenericValue
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    private final Serializable value;

    private GenericValue( final Serializable value )
    {
        this.value = Objects.requireNonNull( value );
    }

    public Optional<GenericValue> optional( final String propertyName )
    {
        return whenMapOrElse( m -> Optional.ofNullable( m.get( propertyName ) ), Optional::empty );
    }

    public GenericValue property( final String propertyName )
    {
        return whenMapOrElse( m -> m.get( propertyName ), () -> {
            throw new NoSuchElementException();
        } );
    }

    public Set<Map.Entry<String, GenericValue>> getProperties()
    {
        return whenMapOrElse( Map::entrySet, ImmutableSet::of );
    }

    public List<GenericValue> asList()
    {
        return whenListOrElse( Function.identity(), () -> ImmutableList.of( this ) );
    }

    public String asString()
    {
        return switch ( value )
        {
            case String s -> s;
            case Long l -> Long.toString( l );
            case Integer i -> Integer.toString( i );
            case Double d -> Double.toString( d );
            case Boolean b -> Boolean.toString( b );
            default -> throw new IllegalStateException();
        };
    }

    public double asDouble()
    {
        return switch ( value )
        {
            case Double d -> d;
            case Long l -> l;
            case Integer i -> i;
            case String s -> Double.parseDouble( s );
            default -> throw new IllegalStateException();
        };
    }

    public int asInteger()
    {
        return switch ( value )
        {
            case Integer i -> i;
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
            case Integer i -> i;
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

    public List<String> asStringList()
    {
        return asList().stream().map( GenericValue::asString ).collect( ImmutableList.toImmutableList() );
    }

    public Object rawJava()
    {
        return switch ( value )
        {
            case String s -> s;
            case Long l -> l;
            case Integer i -> i;
            case Double d -> d;
            case Boolean b -> b;
            case List<?> l -> asList().stream().map( GenericValue::rawJava ).collect( ImmutableList.toImmutableList() );
            case Map<?, ?> m ->
                getProperties().stream().collect( ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> e.getValue().rawJava() ) );
            default -> throw new AssertionError( value );
        };
    }

    public Object rawJs()
    {
        return switch ( value )
        {
            case String s -> s;
            case Long l -> l.doubleValue();
            case Integer i -> i;
            case Double d -> d;
            case Boolean b -> b;
            case List<?> l -> asList().stream().map( GenericValue::rawJs ).collect( ImmutableList.toImmutableList() );
            case Map<?, ?> m ->
                getProperties().stream().collect( ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> e.getValue().rawJs() ) );
            default -> throw new AssertionError( value );
        };
    }

    public Type getType()
    {
        return switch ( value )
        {
            case String s -> Type.STRING;
            case Long l -> Type.NUMBER;
            case Integer i -> Type.NUMBER;
            case Double d -> Type.NUMBER;
            case Boolean b -> Type.BOOLEAN;
            case List<?> l -> Type.LIST;
            case Map<?, ?> m -> Type.OBJECT;
            default -> throw new AssertionError( value );
        };
    }

    @SuppressWarnings("unchecked")
    private <T> T whenMapOrElse( final Function<Map<String, GenericValue>, T> then, final Supplier<T> orElse )
    {
        return value instanceof Map ? then.apply( (Map<String, GenericValue>) value ) : orElse.get();
    }

    @SuppressWarnings("unchecked")
    private <T> T whenListOrElse( final Function<List<GenericValue>, T> then, final Supplier<T> orElse )
    {
        return value instanceof List ? then.apply( (List<GenericValue>) value ) : orElse.get();
    }

    public static GenericValue numberValue( final long value )
    {
        return new GenericValue( (int) value == value ? (int) value : value );
    }

    public static GenericValue numberValue( final double value )
    {
        return new GenericValue( value );
    }

    public static GenericValue stringValue( final String value )
    {
        return new GenericValue( value );
    }

    public static GenericValue booleanValue( final boolean value )
    {
        return new GenericValue( value );
    }

    public static GenericValue stringArray( final Collection<String> value )
    {
        final var list = list();
        value.stream().map( GenericValue::stringValue ).forEach( list::add );
        return list.build();
    }

    public static GenericValue fromRawJava( final Object obj )
    {
        return switch ( obj )
        {
            case String s -> GenericValue.stringValue( s );
            case Boolean b -> GenericValue.booleanValue( b );
            case Byte b -> GenericValue.numberValue( b.intValue() );
            case Short s -> GenericValue.numberValue( s.intValue() );
            case Integer i -> GenericValue.numberValue( i );
            case Long l -> GenericValue.numberValue( l );
            case Float f -> GenericValue.numberValue( f );
            case Double d -> GenericValue.numberValue( d );

            case Collection<?> c ->
            {
                // TODO prevent heterogeneous collections
                final var builder = GenericValue.list();
                c.stream().map( GenericValue::fromRawJava ).forEach( builder::add );
                yield builder.build();
            }
            case Map<?, ?> m ->
            {
                final var builder = GenericValue.object();
                m.forEach( ( key, value ) -> builder.put( (String) key, fromRawJava( value ) ) );
                yield builder.build();
            }
            default -> throw new IllegalArgumentException( "Unsupported object type: " + obj );
        };
    }

    public static ListBuilder list()
    {
        return new ListBuilder();
    }

    public static ObjectBuilder object()
    {
        return new ObjectBuilder();
    }

    public enum Type
    {
        STRING, NUMBER, BOOLEAN, LIST, OBJECT
    }

    @Override
    public boolean equals( final Object o )
    {
        return o instanceof final GenericValue that && Objects.equals( value, that.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( value );
    }

    public static final class ListBuilder
    {
        private final ImmutableList.Builder<GenericValue> builder = ImmutableList.builder();

        private ListBuilder()
        {
        }

        public ListBuilder add( final GenericValue value )
        {
            builder.add( value );
            return this;
        }

        public GenericValue build()
        {
            return new GenericValue( builder.build() );
        }
    }

    public static final class ObjectBuilder
    {
        private final ImmutableMap.Builder<String, GenericValue> builder = ImmutableMap.builder();

        private ObjectBuilder()
        {
        }

        public ObjectBuilder put( final String key, final String value )
        {
            builder.put( Objects.requireNonNull( key ), GenericValue.stringValue( value ) );
            return this;
        }

        public ObjectBuilder put( final String key, final long value )
        {
            builder.put( Objects.requireNonNull( key ), GenericValue.numberValue( value ) );
            return this;
        }

        public ObjectBuilder put( final String key, final GenericValue value )
        {
            builder.put( Objects.requireNonNull( key ), Objects.requireNonNull( value ) );
            return this;
        }

        public ObjectBuilder putArray( final String key, final List<String> value )
        {
            builder.put( Objects.requireNonNull( key ), GenericValue.stringArray( value ) );
            return this;
        }

        public GenericValue build()
        {
            return new GenericValue( ImmutableMap.copyOf( builder.build() ) );
        }
    }
}
