package com.enonic.xp.inputtype;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSetMultimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class InputTypeConfig
    implements Iterable<InputTypeProperty>
{
    private final ImmutableSetMultimap<String, InputTypeProperty> map;

    private InputTypeConfig( final Builder builder )
    {
        this.map = builder.map.build();
    }

    public Set<String> getNames()
    {
        return this.map.keySet();
    }

    public InputTypeProperty getProperty( final String name )
    {
        final Set<InputTypeProperty> properties = getProperties( name );
        return properties.isEmpty() ? null : properties.iterator().next();
    }

    public Set<InputTypeProperty> getProperties( final String name )
    {
        return this.map.get( name );
    }

    private Stream<InputTypeProperty> findProperties( final String name, final Predicate<InputTypeProperty> filter )
    {
        return getProperties( name ).stream().filter( filter );
    }

    public String getValue( final String name )
    {
        final InputTypeProperty property = getProperty( name );

        if ( property == null )
        {
            return null;
        }

        if ( property.getValue() instanceof StringPropertyValue(String value) )
        {
            return value;
        }

        throw new IllegalArgumentException( "Invalid property type: " + property.getValue() );
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue( final String name, final Class<T> type )
    {
        final InputTypeProperty property = getProperty( name );

        if ( property == null )
        {
            return null;
        }

        final PropertyValue propertyValue = property.getValue();
        if ( type == String.class && propertyValue instanceof StringPropertyValue(String value) )
        {
            return (T) value;
        }
        else if ( type == Boolean.class && propertyValue instanceof BooleanPropertyValue(boolean value) )
        {
            return (T) Boolean.valueOf( value );
        }
        else if ( type == Integer.class && propertyValue instanceof IntegerPropertyValue(int value) )
        {
            return (T) Integer.valueOf( value );
        }
        else if ( type == Double.class && propertyValue instanceof DoublePropertyValue(double value) )
        {
            return (T) Double.valueOf( value );
        }
        else if ( type == Long.class && propertyValue instanceof LongPropertyValue(long value) )
        {
            return (T) Long.valueOf( value );
        }
        else if ( List.class.isAssignableFrom( type ) && propertyValue instanceof ListPropertyValue(List<PropertyValue> value) )
        {
            final List<Object> unpacked = value.stream().map( this::unwrapScalarOrComposite ).toList();
            return (T) unpacked;
        }
        else if ( Map.class.isAssignableFrom( type ) && propertyValue instanceof ObjectPropertyValue(Map<String, PropertyValue> value) )
        {
            final Map<String, Object> unpacked = value.entrySet()
                .stream()
                .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, e -> unwrapScalarOrComposite( e.getValue() ) ) );
            return (T) unpacked;
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported property type: " + property.getValue() );
        }
    }

    private Object unwrapScalarOrComposite( PropertyValue pv )
    {
        return switch ( pv )
        {
            case StringPropertyValue spv -> spv.value();
            case BooleanPropertyValue bpv -> bpv.value();
            case IntegerPropertyValue ipv -> ipv.value();
            case DoublePropertyValue dpv -> dpv.value();
            case LongPropertyValue lpv -> lpv.value();
            case ListPropertyValue lpv -> lpv.value().stream().map( this::unwrapScalarOrComposite ).toList();
            case ObjectPropertyValue opv -> opv.value()
                .entrySet()
                .stream()
                .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, e -> unwrapScalarOrComposite( e.getValue() ) ) );
        };
    }

    public <T> T getValue( final String name, final Class<T> type, final T defValue )
    {
        return null;
//        final String value = getValue( name );
//        if ( value == null )
//        {
//            return defValue;
//        }
//
//        final T converted = Converters.convert( value, type );
//        return converted != null ? converted : defValue;
    }

    public boolean hasValue( final String name, final String value )
    {
        return false;
//        return findProperties( name, property -> Objects.equals( value, property.getValue() ) ).count() > 0;
    }

    public boolean hasAttributeValue( final String name, final String attr, final String attrValue )
    {
        return false;
//        return findProperties( name, property -> Objects.equals( attrValue, property.getAttribute( attr ) ) ).count() > 0;
    }

    public int getSize()
    {
        return this.map.size();
    }

    @Override
    public Iterator<InputTypeProperty> iterator()
    {
        return this.map.values().iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof InputTypeConfig ) )
        {
            return false;
        }
        final InputTypeConfig that = (InputTypeConfig) o;
        return map.equals( that.map );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( map );
    }

    public static InputTypeConfig empty()
    {
        return create().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSetMultimap.Builder<String, InputTypeProperty> map = ImmutableSetMultimap.builder();

        private Builder()
        {
        }

        public Builder config( final InputTypeConfig config )
        {
            if ( config != null )
            {
                this.map.putAll( config.map );
            }

            return this;
        }

        public Builder property( final InputTypeProperty property )
        {
            this.map.put( property.getName(), property );
            return this;
        }

        public InputTypeConfig build()
        {
            return new InputTypeConfig( this );
        }
    }
}
