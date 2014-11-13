package com.enonic.wem.api.data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.util.GeoPoint;

public final class Property
    extends Data<Property>
{
    private Value value;

    private Property( final Property source )
    {
        super( source );
        this.value = source.value;
    }

    public Property( final String name, final Value value )
    {
        super( name );
        Preconditions.checkNotNull( value, "value cannot be null" );
        this.value = value;
    }

    public static Property newProperty( final String name, final Value value )
    {
        return new Property( name, value );
    }

    public static Property newGeoPoint( final String name, final Object value )
    {
        return newProperty( name, Value.newGeoPoint( value ) );
    }

    public static Property newInstant( final String name, final Object value )
    {
        return newProperty( name, Value.newInstant( value ) );
    }

    public static Property newLocalDate( final String name, final Object value )
    {
        return newProperty( name, Value.newLocalDate( value ) );
    }

    public static Property newLocalDateTime( final String name, final Object value )
    {
        return newProperty( name, Value.newLocalDateTime( value ) );
    }

    public static Property newLocalTime( final String name, final Object value )
    {
        return newProperty( name, Value.newLocalTime( value ) );
    }

    public static Property newHtmlPart( final String name, final Object value )
    {
        return newProperty( name, Value.newHtmlPart( value ) );
    }

    public static Property newDouble( final String name, final Object value )
    {
        return newProperty( name, Value.newDouble( value ) );
    }

    public static Property newBoolean( final String name, final Object value )
    {
        return newProperty( name, Value.newBoolean( value ) );
    }

    public static Property newString( final String name, final Object value )
    {
        return newProperty( name, Value.newString( value ) );
    }

    public static Property newXml( final String name, final Object value )
    {
        return newProperty( name, Value.newXml( value ) );
    }

    public static Property newLong( final String name, final Object value )
    {
        return newProperty( name, Value.newLong( value ) );
    }

    public static Property newData( final String name, final Object value )
    {
        return newProperty( name, Value.newData( value ) );
    }

    public ValueType getValueType()
    {
        return value.getType();
    }

    @Override
    public PropertyArray getArray()
    {
        return (PropertyArray) super.getArray();
    }

    public boolean hasNullValue()
    {
        return this.value.isNull();
    }

    public Value getValue()
    {
        return value;
    }

    public void setValue( final Value value )
    {
        Preconditions.checkNotNull( value, "A Property cannot have a null value" );
        this.value = value;
    }

    public Value getValue( final int arrayIndex )
    {
        final PropertyArray array = getArray();
        return array.getValue( arrayIndex );
    }

    public Object getObject()
    {
        return value.getObject();
    }

    public String getString()
    {
        return value.asString();
    }

    public ContentId getContentId()
    {
        return value.asContentId();
    }

    /**
     * Returns the value of the Property at the given array index as a String.
     */
    public String getString( final int arrayIndex )
    {
        return getArray().getValue( arrayIndex ).asString();
    }

    public ContentId getContentId( final int arrayIndex )
    {
        return getArray().getValue( arrayIndex ).asContentId();
    }

    public Long getLong()
    {
        return value.asLong();
    }

    /**
     * Returns the value at of the Property at the given array index as a Long.
     */
    public Long getLong( final int arrayIndex )
    {
        return getArray().getValue( arrayIndex ).asLong();
    }

    public Boolean getBoolean()
    {
        return value.asBoolean();
    }

    public Double getDouble()
    {
        return value.asDouble();
    }

    /**
     * Returns the value at of the Property at the given array index as a Double.
     */
    public Double getDouble( final int arrayIndex )
    {
        return getArray().getValue( arrayIndex ).asDouble();
    }

    public GeoPoint getGeoPoint()
    {
        return value.asGeoPoint();
    }

    public LocalDate getLocalDate()
    {
        return value.asLocalDate();
    }

    public LocalTime getLocalTime()
    {
        return value.asLocalTime();
    }

    public LocalDateTime getLocalDateTime()
    {
        return value.asLocalDateTime();
    }

    /**
     * Returns the value at of the Property at the given array index as a DateMidnight.
     */
    public LocalDate getLocalDate( final int arrayIndex )
    {
        return getArray().getValue( arrayIndex ).asLocalDate();
    }

    public Instant getInstant( final int arrayIndex )
    {
        return getArray().getValue( arrayIndex ).asInstant();
    }

    public Instant getInstant()
    {
        return value.asInstant();
    }

    /**
     * Returns the value of the Property at the given array index.
     *
     * @see com.enonic.wem.api.data.Value#asData()
     */
    public RootDataSet getData( final int arrayIndex )
        throws ClassCastException
    {
        return getArray().getValue( arrayIndex ).asData();
    }

    /**
     * @see com.enonic.wem.api.data.Value#asData()
     */
    public RootDataSet getData()
        throws ClassCastException
    {
        return value.asData();
    }

    @Override
    public Property copy()
    {
        final boolean isRootDataSetProperty = this.getValueType().getJavaType().equals( RootDataSet.class );
        if ( isRootDataSetProperty )
        {
            final Value value = this.getValue();
            final RootDataSet rootDataSetValue = value.asData().copy().toRootDataSet();
            return new Property( this.getName(), Value.newValue( value.getType(), rootDataSetValue ) );
        }
        return new Property( this );
    }

    public boolean valueEquals( final com.enonic.wem.api.data.Data data )
    {
        final Property other = data.toProperty();
        return other.getValue().equals( this.getValue() );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( getClass() != o.getClass() )
        {
            return false;
        }

        final Property property = (Property) o;

        return super.equals( o ) && Objects.equals( value, property.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), value );
    }

    @Override
    public String toString()
    {
        final com.google.common.base.Objects.ToStringHelper s = com.google.common.base.Objects.toStringHelper( this );
        s.add( "name", getName() );
        s.add( "type", getValueType() );
        s.add( "value", value.getObject() );
        return s.toString();
    }
}
