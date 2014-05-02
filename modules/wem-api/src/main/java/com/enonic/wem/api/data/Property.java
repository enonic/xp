package com.enonic.wem.api.data;

import java.util.Objects;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.data.type.InconvertibleValueException;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.entity.EntityId;

public class Property
    extends Data<Property>
{
    private Value value;

    Property( final Property source )
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

    public ValueType getValueType()
    {
        return value.getType();
    }

    public void setValue( final Value value )
    {
        Preconditions.checkNotNull( value, "A Property cannot have a null value" );
        this.value = value;
    }

    @Override
    public PropertyArray getArray()
    {
        return (PropertyArray) super.getArray();
    }

    public Value getValue()
    {
        return value;
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
        throws InconvertibleValueException
    {
        return value.asString();
    }

    public ContentId getContentId()
        throws InconvertibleValueException
    {
        return value.asContentId();
    }

    public EntityId getEntityId()
        throws InconvertibleValueException
    {
        return value.asEntityId();
    }

    /**
     * Returns the value of the Property at the given array index as a String.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a String.
     */
    public String getString( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asString();
    }

    public ContentId getContentId( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asContentId();
    }

    public EntityId getEntityId( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asEntityId();
    }

    public Long getLong()
        throws InconvertibleValueException
    {
        return value.asLong();
    }

    /**
     * Returns the value at of the Property at the given array index as a Long.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a Long.
     */
    public Long getLong( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asLong();
    }

    public Boolean getBoolean()
        throws InconvertibleValueException
    {
        return value.asBoolean();
    }

    public Double getDouble()
        throws InconvertibleValueException
    {
        return value.asDouble();
    }

    /**
     * Returns the value at of the Property at the given array index as a Double.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a Double.
     */
    public Double getDouble( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDouble();
    }

    public DateMidnight getDateMidnight()
        throws InconvertibleValueException
    {
        return value.asDateMidnight();
    }

    /**
     * Returns the value at of the Property at the given array index as a DateMidnight.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a org.joda.time.DateMidnight.
     */
    public DateMidnight getDateMidnight( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDateMidnight();
    }

    public DateTime getDateTime()
        throws InconvertibleValueException
    {
        return value.asDateTime();
    }

    /**
     * Returns the value at of the Property at the given array index as a DateTime.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a org.joda.time.DateTime.
     */
    public DateTime getDateTime( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDateTime();
    }

    /**
     * Returns the value of the Property at the given array index.
     *
     * @see com.enonic.wem.api.data.Value#getData()
     */
    public RootDataSet getData( final int arrayIndex )
        throws ClassCastException
    {
        return getArray().getValue( arrayIndex ).getData();
    }

    /**
     * @see com.enonic.wem.api.data.Value#getData()
     */
    public RootDataSet getData()
        throws ClassCastException
    {
        return value.getData();
    }

    @Override
    public Property copy()
    {
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

    public static Property newProperty( final String name, final Value value )
    {
        return new Property( name, value );
    }

    public static Property newGeoPoint( final String name, final String value )
    {
        return newProperty( name, Value.newGeoPoint( value ) );
    }

    public static Property newDateTime( final String name, final String value )
    {
        return newProperty( name, Value.newDateTime( value ) );
    }

    public static Property newDateTime( final String name, final DateTime value )
    {
        return newProperty( name, Value.newDateTime( value ) );
    }

    public static Property newDateMidnight( final String name, final String value )
    {
        return newProperty( name, Value.newDateMidnight( value ) );
    }

    public static Property newDateMidnight( final String name, final DateMidnight value )
    {
        return newProperty( name, Value.newDateMidnight( value ) );
    }

    public static Property newContentId( final String name, final String value )
    {
        return newProperty( name, Value.newContentId( value ) );
    }

    public static Property newContentId( final String name, final ContentId value )
    {
        return newProperty( name, Value.newContentId( value ) );
    }

    public static Property newEntityId( final String name, final String value )
    {
        return newProperty( name, Value.newEntityId( value ) );
    }

    public static Property newEntityId( final String name, final EntityId value )
    {
        return newProperty( name, Value.newEntityId( value ) );
    }

    public static Property newHtmlPart( final String name, final String value )
    {
        return newProperty( name, Value.newHtmlPart( value ) );
    }

    public static Property newDouble( final String name, final Number value )
    {
        return newProperty( name, Value.newDouble( value ) );
    }

    public static Property newBoolean( final String name, final Boolean value )
    {
        return newProperty( name, Value.newBoolean( value ) );
    }

    public static Property newString( final String name, final String value )
    {
        return newProperty( name, Value.newString( value ) );
    }

    public static Property newXml( final String name, final String value )
    {
        return newProperty( name, Value.newXml( value ) );
    }

    public static Property newLong( final String name, final Number value )
    {
        return newProperty( name, Value.newLong( value ) );
    }

    public static Property newData( final String name, final RootDataSet value )
    {
        return newProperty( name, Value.newData( value ) );
    }
}
