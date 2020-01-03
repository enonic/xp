package com.enonic.xp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PropertyArray
{
    private PropertyTree tree;

    private final PropertySet parent;

    private final String name;

    private final ValueType valueType;

    private final List<Property> array = new ArrayList<>();

    PropertyArray( final PropertyTree tree, final PropertySet parent, final String name, final ValueType valueType )
    {
        Preconditions.checkNotNull( parent, "parent cannot be null" );
        Preconditions.checkNotNull( name, "name cannot be null" );
        Preconditions.checkNotNull( valueType, "valueType cannot be null" );

        this.tree = tree;
        this.parent = parent;
        Property.checkName( name );
        this.name = name;
        this.valueType = valueType;
    }

    /**
     * Copy constructor.
     */
    private PropertyArray( final PropertyArray source, final PropertyTree tree, final PropertySet parent )
    {
        Preconditions.checkNotNull( source, "source cannot be null" );
        Preconditions.checkNotNull( tree, "tree cannot be null" );
        Preconditions.checkNotNull( parent, "parent cannot be null" );
        this.tree = tree;
        this.parent = parent;
        this.name = source.name;
        this.valueType = source.valueType;
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

    void detach()
    {
        this.tree = null;
        for ( final Property p : array )
        {
            p.detach();
        }
    }

    @Override
    public String toString()
    {
        final boolean isPropertySet = valueType.equals( ValueTypes.PROPERTY_SET );
        final boolean parentIsPropertySet =
            parent.getProperty() != null && parent.getProperty().getType().equals( ValueTypes.PROPERTY_SET );

        final StringBuilder s = new StringBuilder();
        final String indent = " ".repeat( ( countAncestors() + 1 ) * 2 );
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

    public int countAncestors()
    {
        return parent.getProperty() != null ? parent.countAncestors() + 1 : 0;
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

    public ImmutableList<Property> getProperties()
    {
        return ImmutableList.copyOf( array );
    }

    public ImmutableList<Value> getValues()
    {
        ImmutableList.Builder<Value> builder = new ImmutableList.Builder<>();
        for ( final Property p : array )
        {
            builder.add( p.getValue() );
        }
        return builder.build();
    }

    /**
     * Creates a new PropertySet attached to the same PropertyTree as this PropertyArray.
     */
    PropertySet newSet()
    {
        return this.parent.newSet();
    }

    void addProperty( final Property property )
    {
        checkType( property.getType() );
        this.array.add( property );

        if ( tree != null )
        {
            if ( property.getValue().isPropertySet() )
            {
                final PropertySet set = property.getSet();
                if ( set != null )
                {
                    set.setPropertyTree( tree );
                }
            }
        }
    }

    Property addValue( final Value value )
    {
        checkType( value.getType() );
        final Property property = new Property( name, this.array.size(), value, parent );
        this.array.add( property );
        if ( tree != null )
        {
            if ( value.getObject() instanceof PropertySet )
            {
                final PropertySet set = (PropertySet) value.getObject();
                set.setPropertyTree( tree );
            }
        }
        return property;
    }

    Property setValue( final int index, final Value value )
    {
        checkType( value.getType() );

        final Property existing = get( index );
        if ( get( index ) != null )
        {
            existing.setValue( value );
            return existing;
        }
        else
        {
            final Property newProperty = new Property( name, index, value, parent );
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
        final Property property = array.get( index );
        array.remove( index );
    }

    void removeAll()
    {
        for ( int index = array.size() - 1; index >= 0; index-- )
        {
            final Property property = array.get( index );
            array.remove( index );
        }
    }

    private void checkType( final ValueType valueType )
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
    PropertyArray copy( final PropertyTree tree, final PropertySet parent )
    {
        return new PropertyArray( this, tree, parent );
    }
}
