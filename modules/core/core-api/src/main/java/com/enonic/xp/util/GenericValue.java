package com.enonic.xp.util;

import java.io.Serial;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.math.DoubleMath;

/**
 * A generic value wrapper that can hold different types of values such as String, Number, Boolean, List, and Map, similar to JSON values.
 * It provides methods to access and convert the underlying value in a type-safe manner.
 * It is serializable and can be constructed using static factory methods or builders for lists and objects.
 * Null values are not supported.
 */
public final class GenericValue
    implements Serializable
{
    private static final GenericValue TRUE = new GenericValue( Boolean.TRUE );

    private static final GenericValue FALSE = new GenericValue( Boolean.FALSE );

    private static final GenericValue EMPTY_LIST = new GenericValue( ImmutableList.of() );

    private static final GenericValue EMPTY_OBJECT = new GenericValue( ImmutableMap.of() );

    @Serial
    private static final long serialVersionUID = 0;

    private final Serializable value;

    private GenericValue( final Serializable value )
    {
        this.value = Objects.requireNonNull( value );
    }

    /**
     * The type of value stored in this GenericValue.
     */
    public enum Type
    {
        STRING, NUMBER, BOOLEAN, LIST, OBJECT
    }

    /**
     * Gets a property from this GenericValue assuming it is an object (map).
     *
     * @param propertyName the name of the property to get
     * @return the property value
     * @throws NoSuchElementException if this GenericValue is not an object or if the property does not exist
     */
    public GenericValue property( final String propertyName )
    {
        return optional( propertyName ).orElseThrow( () -> new NoSuchElementException( propertyName ) );
    }

    /**
     * Gets an optional property from this GenericValue assuming it is an object (map).
     *
     * @param propertyName the name of the property to get
     * @return an Optional containing the property value if it exists, or an empty Optional if it does not exist or if this GenericValue is not an object
     */
    public Optional<GenericValue> optional( final String propertyName )
    {
        return whenMapOrElse( m -> Optional.ofNullable( m.get( propertyName ) ), Optional::empty );
    }

    /**
     * Gets all properties from this GenericValue assuming it is an object (map).
     *
     * @return a set of map entries representing the properties, or an empty set if this GenericValue is not an object
     */
    public Set<Map.Entry<String, GenericValue>> properties()
    {
        return whenMapOrElse( Map::entrySet, ImmutableSet::of );
    }

    /**
     * Gets all elements from this GenericValue assuming it is a list (array).
     *
     * @return a list of GenericValue elements, or a list containing this GenericValue if it is not a list
     */
    public List<GenericValue> values()
    {
        return whenListOrElse( Function.identity(), () -> ImmutableList.of( this ) );
    }

    /**
     * Converts this GenericValue to a string representation.
     *
     * @return the string representation of the value
     * @throws IllegalStateException if the value is not of a type that can be converted to a string ( object or list )
     */
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

    /**
     * Converts this GenericValue to a double representation.
     *
     * @return the double representation of the value
     * @throws IllegalStateException if the value is not of a type that can be converted to a double ( boolean, object or list )
     * @throws NumberFormatException if the value is a string that cannot be parsed as a double
     */
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

    /**
     * Converts this GenericValue to an integer representation.
     * double values decimal part will be rounded down.
     *
     * @return the integer representation of the value
     * @throws IllegalStateException if the value is not of a type that can be converted to an integer ( boolean, object or list )
     * @throws NumberFormatException if the value is a string that cannot be parsed as an integer
     * @throws ArithmeticException   if the value is a long or double that is out of integer range
     */
    public int asInteger()
    {
        return switch ( value )
        {
            case Integer i -> i;
            case Long l -> Math.toIntExact( l );
            case Double d -> DoubleMath.roundToInt( d, RoundingMode.DOWN );
            case String s -> Integer.parseInt( s );
            default -> throw new IllegalStateException();
        };
    }

    /**
     * Converts this GenericValue to a long representation.
     * double values decimal part will be rounded down.
     *
     * @return the long representation of the value
     * @throws IllegalStateException if the value is not of a type that can be converted to a long ( boolean, object or list )
     * @throws NumberFormatException if the value is a string that cannot be parsed as a long
     * @throws ArithmeticException   if the value is a double that is out of long range
     */
    public long asLong()
    {
        return switch ( value )
        {
            case Long l -> l;
            case Integer i -> i;
            case Double d -> DoubleMath.roundToLong( d, RoundingMode.DOWN );
            case String s -> Long.parseLong( s );
            default -> throw new IllegalStateException();
        };
    }

    /**
     * Converts this GenericValue to a boolean representation.
     * Note: To avoid ambiguity, only actual boolean values are converted. Strings like "true" or numbers like 1 are not converted.
     * In case you need such conversions use {@link #asString()} method and implement your own logic.
     *
     * @return the boolean representation of the value
     * @throws IllegalStateException if the value is not of a type that can be converted to a boolean ( string, number, object or list )
     */
    public boolean asBoolean()
    {
        return switch ( value )
        {
            case Boolean b -> b;
            default -> throw new IllegalStateException();
        };
    }

    /**
     * A utility method to convert this GenericValue into a list of strings. Single values are converted into a list with one string element.
     *
     * @return list of strings
     * @throws IllegalStateException if the values are not of a type that can be converted to a string ( object or list )
     */
    public List<String> toStringList()
    {
        return values().stream().map( GenericValue::asString ).collect( ImmutableList.toImmutableList() );
    }

    /**
     * Converts this GenericValue into its raw Java representation.
     *
     * @return the raw Java object: String, Long, Integer, Double, Boolean, List, or Map
     */
    public Object toRawJava()
    {
        return switch ( value )
        {
            case String s -> s;
            case Long l -> l;
            case Integer i -> i;
            case Double d -> d;
            case Boolean b -> b;
            case List<?> l -> values().stream().map( GenericValue::toRawJava ).collect( ImmutableList.toImmutableList() );
            case Map<?, ?> m ->
                properties().stream().collect( ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> e.getValue().toRawJava() ) );
            default -> throw new AssertionError( value );
        };
    }

    /**
     * Converts this GenericValue into its raw JavaScript-compatible representation.
     * The difference from rawJava() is that Long values are converted to Double to accommodate JavaScript's number type.
     * Note that this may lead to precision loss for very large long values.
     *
     * @return the raw Java object: String, Double, Integer, Boolean, List, or Map
     */
    public Object toRawJs()
    {
        return switch ( value )
        {
            case String s -> s;
            case Long l -> l.doubleValue();
            case Integer i -> i;
            case Double d -> d;
            case Boolean b -> b;
            case List<?> l -> values().stream().map( GenericValue::toRawJs ).collect( ImmutableList.toImmutableList() );
            case Map<?, ?> m ->
                properties().stream().collect( ImmutableMap.toImmutableMap( Map.Entry::getKey, e -> e.getValue().toRawJs() ) );
            default -> throw new AssertionError( value );
        };
    }

    /**
     * Returns the type of value stored in this GenericValue.
     *
     * @return type of value
     */
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

    /**
     * Creates a GenericValue wrapping a number.
     *
     * @param value number value
     * @return GenericValue wrapping the number
     */
    public static GenericValue numberValue( final long value )
    {
        return (int) value == value ? new GenericValue( (int) value ) : new GenericValue( value );
    }

    /**
     * Creates a GenericValue wrapping a number.
     *
     * @param value number value
     * @return GenericValue wrapping the number
     */
    public static GenericValue numberValue( final double value )
    {
        return new GenericValue( value );
    }

    /**
     * Creates a GenericValue wrapping a string.
     *
     * @param value string value
     * @return GenericValue wrapping the string
     */
    public static GenericValue stringValue( final String value )
    {
        return new GenericValue( value );
    }

    /**
     * Creates a GenericValue wrapping a boolean.
     *
     * @param value boolean value
     * @return GenericValue wrapping the boolean
     */
    public static GenericValue booleanValue( final boolean value )
    {
        return value ? TRUE : FALSE;
    }

    /**
     * A utility method to convert a raw Java object into a GenericValue.
     * * Supported types are String, Boolean, Byte, Short, Integer, Long, Float, Double, Collection, and Map.
     */
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
            case Collection<?> c -> c.stream().map( GenericValue::fromRawJava ).collect( listCollector() );
            case Map<?, ?> m ->
            {
                final var builder = GenericValue.newObject();
                m.forEach( ( key, value ) -> builder.put( (String) key, fromRawJava( value ) ) );
                yield builder.build();
            }
            default -> throw new IllegalArgumentException( "Unsupported object type: " + obj );
        };
    }

    /**
     * Creates a builder for a GenericValue representing a list (array).
     *
     * @return ListBuilder instance
     */
    public static ListBuilder newList()
    {
        return new ListBuilder();
    }

    /**
     * Creates a collector that collects GenericValue elements into a GenericValue representing a list (array).
     *
     * @return Collector instance
     */
    public static Collector<GenericValue, ?, GenericValue> listCollector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), GenericValue::newListInternal );
    }

    /**
     * Creates a builder for a GenericValue representing an object (map).
     *
     * @return ObjectBuilder instance
     */
    public static ObjectBuilder newObject()
    {
        return new ObjectBuilder();
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

    private static GenericValue newListInternal( final ImmutableList<GenericValue> list )
    {
        return list.isEmpty() ? EMPTY_LIST : new GenericValue( list );
    }

    private static GenericValue newObjectInternal( final ImmutableMap<String, GenericValue> map )
    {
        return map.isEmpty() ? EMPTY_OBJECT : new GenericValue( map );
    }

    /**
     * Builder for creating GenericValue instances representing lists (arrays).
     */
    public static final class ListBuilder
    {
        private final ImmutableList.Builder<GenericValue> builder = ImmutableList.builder();

        private ListBuilder()
        {
        }

        /**
         * Adds a GenericValue to the list.
         *
         * @param value GenericValue to add
         * @return the ListBuilder instance for chaining
         */
        public ListBuilder add( final GenericValue value )
        {
            builder.add( value );
            return this;
        }

        /**
         * Builds the GenericValue representing the list.
         *
         * @return GenericValue representing the list
         */
        public GenericValue build()
        {
            return newListInternal( builder.build() );
        }
    }

    /**
     * Builder for creating GenericValue instances representing objects (maps).
     */
    public static final class ObjectBuilder
    {
        private final ImmutableMap.Builder<String, GenericValue> builder = ImmutableMap.builder();

        private ObjectBuilder()
        {
        }

        /**
         * Puts a string key-value pair into the object.
         *
         * @param key   the key
         * @param value the string value
         * @return the ObjectBuilder instance for chaining
         */
        public ObjectBuilder put( final String key, final String value )
        {
            builder.put( Objects.requireNonNull( key ), GenericValue.stringValue( value ) );
            return this;
        }

        /**
         * Puts a number key-value pair into the object.
         *
         * @param key   the key
         * @param value the long value
         * @return the ObjectBuilder instance for chaining
         */
        public ObjectBuilder put( final String key, final long value )
        {
            builder.put( Objects.requireNonNull( key ), GenericValue.numberValue( value ) );
            return this;
        }

        /**
         * Puts a number key-value pair into the object.
         *
         * @param key   the key
         * @param value the double value
         * @return the ObjectBuilder instance for chaining
         */
        public ObjectBuilder put( final String key, final double value )
        {
            builder.put( Objects.requireNonNull( key ), GenericValue.numberValue( value ) );
            return this;
        }

        /**
         * Puts a boolean key-value pair into the object.
         *
         * @param key   the key
         * @param value the boolean value
         * @return the ObjectBuilder instance for chaining
         */
        public ObjectBuilder put( final String key, final boolean value )
        {
            builder.put( Objects.requireNonNull( key ), GenericValue.booleanValue( value ) );
            return this;
        }

        /**
         * Puts a GenericValue key-value pair into the object.
         *
         * @param key   the key
         * @param value the GenericValue
         * @return the ObjectBuilder instance for chaining
         */
        public ObjectBuilder put( final String key, final GenericValue value )
        {
            builder.put( Objects.requireNonNull( key ), Objects.requireNonNull( value ) );
            return this;
        }

        /**
         * Builds the GenericValue representing the object.
         *
         * @return GenericValue representing the object
         */
        public GenericValue build()
        {
            return newObjectInternal( builder.build() );
        }
    }
}
