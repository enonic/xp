package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

import static com.google.common.base.Strings.nullToEmpty;

@PublicApi
public final class Property
{
    private final PropertySet parent;

    private final String name;

    private final int index;

    private Value value;

    Property( final String name, final int index, final Value value, final PropertySet parent )
    {
        checkName( name );
        this.name = name;
        this.index = index;
        this.value = value;
        this.parent = parent;
        setPropertyOnPropertySetValue( value, parent );
    }

    private void setPropertyOnPropertySetValue( final Value value, final PropertySet parent )
    {
        if ( value.isSet() && !value.isNull() )
        {
            final PropertySet data = value.asData();
            final PropertyTree tree = parent.getTree();

            data.setProperty( this );

            if ( tree != null && data == tree.getRoot() )
            {
                throw new IllegalArgumentException( "Given PropertySet is already the root PropertySet of the PropertyTree" );
            }
        }
    }

    void detach()
    {
        if ( value.getType().equals( ValueTypes.PROPERTY_SET ) && !value.isNull() )
        {
            value.asData().detach();
        }
    }

    public void setValue( final Value value )
    {
        this.value = value;
        this.setPropertyOnPropertySetValue( value, this.parent );
    }

    public PropertySet getParent()
    {
        return parent;
    }

    public String getName()
    {
        return name;
    }

    public int getIndex()
    {
        return index;
    }

    public PropertyPath getPath()
    {
        if ( this.parent.getProperty() != null )
        {
            return PropertyPath.from( this.parent.getProperty().getPath(), PropertyPath.Element.from( this.name, this.index ) );
        }
        else
        {
            return PropertyPath.from( PropertyPath.Element.from( this.name, this.index ) );
        }
    }

    public ValueType getType()
    {
        return value.getType();
    }

    public Value getValue()
    {
        return value;
    }

    public boolean hasNullValue()
    {
        return value.isNull();
    }

    public boolean hasNotNullValue()
    {
        return !value.isNull();
    }

    public PropertySet getSet()
    {
        return value.asData();
    }

    public Object getObject()
    {
        return value.getObject();
    }

    public String getString()
    {
        return value.asString();
    }

    public Boolean getBoolean()
    {
        return value.asBoolean();
    }

    public Long getLong()
    {
        return value.asLong();
    }

    public Double getDouble()
    {
        return value.asDouble();
    }

    public GeoPoint getGeoPoint()
    {
        return value.asGeoPoint();
    }

    public Reference getReference()
    {
        return value.asReference();
    }

    public BinaryReference getBinaryReference()
    {
        return value.asBinaryReference();
    }

    public Link getLink()
    {
        return value.asLink();
    }

    public LocalDate getLocalDate()
    {
        return value.asLocalDate();
    }

    public LocalDateTime getLocalDateTime()
    {
        return value.asLocalDateTime();
    }

    public LocalTime getLocalTime()
    {
        return value.asLocalTime();
    }

    public Instant getInstant()
    {
        return value.asInstant();
    }

    public static void checkName( final String name )
    {
        if ( name == null )
        {
            throw new NullPointerException( "Property name cannot be null" );
        }
        if ( nullToEmpty( name ).isBlank() )
        {
            throw new IllegalArgumentException( "Property name cannot be blank" );
        }
        if ( name.contains( "." ) )
        {
            throw new IllegalArgumentException( "Property name cannot contain ." );
        }
        if ( name.contains( "[" ) || name.contains( "]" ) )
        {
            throw new IllegalArgumentException( "Property name cannot contain [ or ]" );
        }
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Property ) )
        {
            return false;
        }

        final Property property = (Property) o;

        if ( !Objects.equals( name, property.name ) )
        {
            return false;
        }
        if ( index != property.index )
        {
            return false;
        }
        return Objects.equals( value, property.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, index, value );
    }

    @Override
    public String toString()
    {
        return name + ": " + value;
    }

    public int countAncestors()
    {
        return getPath().elementCount() - 1;
    }

    public Property copyTo( final PropertySet destination )
    {
        final Value copiedValue = value.copy( destination.getTree() );
        final Property property = new Property( name, index, copiedValue, destination );
        destination.add( property );
        return property;
    }
}
