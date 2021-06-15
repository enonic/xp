package com.enonic.xp.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

@PublicApi
public abstract class Value
{
    private final ValueType type;

    private final Object object;

    protected Value( final ValueType type, final Object value )
    {
        Preconditions.checkNotNull( type, "type cannot be null" );
        if ( value != null )
        {
            Preconditions.checkArgument( !( value instanceof Value ), "The value of a Value cannot be: " + value.getClass() );
            Preconditions.checkArgument( type.getJavaType().isInstance( value ),
                                         "value is of wrong class, expected [" + type.getJavaType().getName() + "], got: " +
                                             value.getClass().getName() );
        }

        this.type = type;
        this.object = value;
    }

    protected Value( final Value value )
    {
        this.type = value.type;
        this.object = value.getObject();
    }

    public boolean isSet()
    {
        return this.type.equals( ValueTypes.PROPERTY_SET );
    }

    public boolean isString()
    {
        return this.type.equals( ValueTypes.STRING );
    }

    public boolean isDateType()
    {
        return this.type.equals( ValueTypes.DATE_TIME ) || this.type.equals( ValueTypes.LOCAL_DATE ) ||
            this.type.equals( ValueTypes.LOCAL_DATE_TIME );
    }

    public boolean isNumericType()
    {
        return ( this.object instanceof Number );
    }

    public boolean isGeoPoint()
    {
        return this.type.equals( ValueTypes.GEO_POINT );
    }

    public boolean isText()
    {
        return type.equals( ValueTypes.STRING ) || type.equals( ValueTypes.XML );
    }

    public boolean isBoolean()
    {
        return type.equals( ValueTypes.BOOLEAN );
    }

    public boolean isJavaType( final Class javaType )
    {
        return javaType.isInstance( this.object );
    }


    public ValueType getType()
    {
        return type;
    }

    public Object getObject()
    {
        return object;
    }

    Object toJsonValue()
    {
        return object;
    }

    public PropertySet asData()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.PROPERTY_SET.convert( object );
    }

    public String asString()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.STRING.convert( object );
    }

    public Long asLong()
    {
        if ( object == null || "".equals( object ) )
        {
            return null;
        }
        return ValueTypes.LONG.convert( object );
    }

    public Boolean asBoolean()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.BOOLEAN.convert( object );
    }

    public Double asDouble()
    {
        if ( object == null || "".equals( object ) )
        {
            return null;
        }
        return ValueTypes.DOUBLE.convert( object );
    }

    public LocalDate asLocalDate()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LOCAL_DATE.convert( object );
    }

    public LocalTime asLocalTime()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LOCAL_TIME.convert( object );
    }

    public LocalDateTime asLocalDateTime()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LOCAL_DATE_TIME.convert( object );
    }

    public Instant asInstant()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.DATE_TIME.convert( object );
    }

    public GeoPoint asGeoPoint()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.GEO_POINT.convert( object );
    }

    public Reference asReference()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.REFERENCE.convert( object );
    }


    public BinaryReference asBinaryReference()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.BINARY_REFERENCE.convert( object );
    }

    public Link asLink()
    {
        if ( object == null )
        {
            return null;
        }
        return ValueTypes.LINK.convert( object );
    }

    abstract Value copy( PropertyTree tree );

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Value ) )
        {
            return false;
        }

        final Value other = (Value) o;

        return Objects.equals( type, other.type ) && Objects.equals( object, other.object );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, object );
    }

    @Override
    public String toString()
    {
        return asString();
    }

    public boolean isNull()
    {
        return this.object == null;
    }

    public boolean isPropertySet()
    {
        return type.equals( ValueTypes.PROPERTY_SET );
    }
}
