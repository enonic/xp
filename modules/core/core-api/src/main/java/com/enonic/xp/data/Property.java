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

@PublicApi
public final class Property
{
    private final PropertyArray array;

    private final int index;

    private Value value;

    Property( final int index, final Value value, final PropertyArray array )
    {
        this.index = index;
        this.value = value;
        this.array = array;
        setPropertyOnPropertySetValue();
    }

    private void setPropertyOnPropertySetValue()
    {
        final Object valueObject = value.getObject();
        if (valueObject instanceof PropertySet)
        {
            final PropertySet propertySet = (PropertySet) valueObject;

            final PropertyTree tree = array.getParent().getTree();
            if ( tree != null && propertySet == tree.getRoot() )
            {
                throw new IllegalArgumentException( "Given PropertySet is the root PropertySet of the PropertyTree" );
            }
            propertySet.setProperty( this );
        }
    }

    public void setValue( final Value value )
    {
        this.array.checkType( value.getType() );
        this.value = value;
        this.setPropertyOnPropertySetValue();
    }

    public PropertySet getParent()
    {
        return array.getParent();
    }

    public String getName()
    {
        return array.getName();
    }

    public int getIndex()
    {
        return index;
    }

    public PropertyPath getPath()
    {
        if ( array.getParent().getProperty() != null )
        {
            return PropertyPath.from( this.array.getParent().getProperty().getPath(), PropertyPath.Element.from( array.getName(), index ) );
        }
        else
        {
            return PropertyPath.from( PropertyPath.Element.from( array.getName(), index ) );
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
        if ( name.isBlank() )
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

        if ( !Objects.equals( this.array.getName(), property.array.getName() ) )
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
        return Objects.hash( array.getName(), index, value );
    }

    @Override
    public String toString()
    {
        return array.getName() + ": " + value;
    }

    public Property copyTo( final PropertySet destination )
    {
        return destination.addProperty( array.getName(), value.copy( destination.getTree() ) );
    }
}
