package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PropertyArray
{
    private final PropertySet parent;

    private final String name;

    private final ValueType valueType;

    private final List<Property> array;

    PropertyArray( final PropertySet parent, final String name, final ValueType valueType, final int initialCapacity )
    {
        Objects.requireNonNull( parent, "parent cannot be null" );
        Objects.requireNonNull( name, "name cannot be null" );
        Objects.requireNonNull( valueType, "valueType cannot be null" );
        Property.checkName( name );

        this.parent = parent;
        this.name = name;
        this.valueType = valueType;
        this.array = new ArrayList<>( initialCapacity );
    }

    /**
     * Copy constructor.
     */
    private PropertyArray( final PropertyArray source, final PropertySet parent )
    {
        Objects.requireNonNull( source, "source cannot be null" );
        Objects.requireNonNull( parent, "parent cannot be null" );

        this.parent = parent;
        this.name = source.name;
        this.valueType = source.valueType;
        this.array = new ArrayList<>( source.array.size() );
        for ( final Property sourceProperty : source.array )
        {
            array.add( sourceProperty.copyTo( parent ) );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PropertyArray ) )
        {
            return false;
        }

        final PropertyArray that = (PropertyArray) o;

        return Objects.equals( name, that.name ) &&
            Objects.equals( valueType, that.valueType ) &&
            Objects.equals( array, that.array );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, valueType, array );
    }

    @Override
    public String toString()
    {
        final boolean isPropertySet = valueType.equals( ValueTypes.PROPERTY_SET );
        final boolean parentIsPropertySet =
            parent.getProperty() != null && parent.getProperty().getType().equals( ValueTypes.PROPERTY_SET );

        final StringBuilder s = new StringBuilder();
        final String indent = " ".repeat( ( parent.getProperty() != null ? parent.getProperty().getPath().elementCount() + 1 : 1 ) * 2 );
        s.append( indent );
        if ( parentIsPropertySet )
        {
            s.append( "  " );
            if ( !isPropertySet )
            {
                s.append( "  " );
            }
        }
        s.append( name ).append( ": [" );
        for ( int i = 0; i < array.size(); i++ )
        {
            final Property p = array.get( i );
            if ( isPropertySet )
            {
                s.append( "\n" ).append( parentIsPropertySet ? indent + "  " : indent ).append( "  [" );
            }
            s.append( isPropertySet ? p.getSet() : p.getValue() );
            if ( isPropertySet )
            {
                s.append( "\n" ).append( parentIsPropertySet ? indent + "  " : indent ).append( "  ]" );
            }
            if ( i < array.size() - 1 )
            {
                s.append( "," ).append( !isPropertySet ? " " : "" );
            }
        }

        if ( isPropertySet )
        {
            s.append( "\n" ).append( indent );
            s.append( parentIsPropertySet ? "  " : "" );
        }
        s.append( "]" );
        return s.toString();
    }

    PropertySet getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public ValueType getValueType()
    {
        return valueType;
    }

    public List<Property> getProperties()
    {
        return ImmutableList.copyOf( array );
    }

    public List<Value> getValues()
    {
        ImmutableList.Builder<Value> builder = new ImmutableList.Builder<>();
        for ( final Property p : array )
        {
            builder.add( p.getValue() );
        }
        return builder.build();
    }

    Property addValue( final Value value )
    {
        checkType( value.getType() );

        final Property property = new Property( array.size(), value, this );
        this.array.add( property );
        return property;
    }

    Property setValue( final int index, final Value value )
    {
        checkType( value.getType() );

        final Property existing = get( index );
        if ( existing != null )
        {
            existing.setValue( value );
            return existing;
        }
        else
        {
            final Property newProperty = new Property( index, value, this );
            this.array.add( index, newProperty );
            return newProperty;
        }
    }

    public int size()
    {
        return array.size();
    }

    public Property get( final int index )
    {
        if ( index >= array.size() )
        {
            return null;
        }
        return array.get( index );
    }


    void remove( final int index )
    {
        array.remove( index );
    }

    void checkType( final ValueType valueType )
    {
        if ( !valueType.equals( this.valueType ) )
        {
            throw new IllegalArgumentException(
                "This PropertyArray expects only properties with value of type '" + this.valueType + "', got: " +
                    valueType );
        }
    }

    /**
     * Makes a copy of this PropertyArray, attaches it to the given PropertyTree and makes the given PropertySet it's parent.
     */
    PropertyArray copy( final PropertySet parent )
    {
        return new PropertyArray( this, parent );
    }
}
