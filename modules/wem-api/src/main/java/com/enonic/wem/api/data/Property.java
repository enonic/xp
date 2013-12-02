package com.enonic.wem.api.data;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.type.InconvertibleValueException;
import com.enonic.wem.api.data.type.ValueType;

public class Property
    extends Data<Property>
{
    private Value value;

    Property( final Property source )
    {
        super( source );
        this.value = source.value;
    }

    public Property( final java.lang.String name, final Value value )
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

    public java.lang.String getString()
        throws InconvertibleValueException
    {
        return value.asString();
    }

    public com.enonic.wem.api.content.ContentId getContentId()
        throws InconvertibleValueException
    {
        return value.asContentId();
    }

    public com.enonic.wem.api.entity.EntityId getEntityId()
        throws InconvertibleValueException
    {
        return value.asEntityId();
    }

    /**
     * Returns the value of the Property at the given array index as a String.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a String.
     */
    public java.lang.String getString( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asString();
    }

    public com.enonic.wem.api.content.ContentId getContentId( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asContentId();
    }

    public com.enonic.wem.api.entity.EntityId getEntityId( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asEntityId();
    }

    public java.lang.Long getLong()
        throws InconvertibleValueException
    {
        return value.asLong();
    }

    /**
     * Returns the value at of the Property at the given array index as a Long.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a Long.
     */
    public java.lang.Long getLong( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asLong();
    }

    public java.lang.Double getDouble()
        throws InconvertibleValueException
    {
        return value.asDouble();
    }

    /**
     * Returns the value at of the Property at the given array index as a Double.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a Double.
     */
    public java.lang.Double getDouble( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDouble();
    }

    public org.joda.time.DateMidnight getDateMidnight()
        throws InconvertibleValueException
    {
        return value.asDateMidnight();
    }

    /**
     * Returns the value at of the Property at the given array index as a DateMidnight.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a org.joda.time.DateMidnight.
     */
    public org.joda.time.DateMidnight getDateMidnight( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDateMidnight();
    }

    public org.joda.time.DateTime getDateTime()
        throws InconvertibleValueException
    {
        return value.asDateTime();
    }

    /**
     * Returns the value at of the Property at the given array index as a DateTime.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a org.joda.time.DateTime.
     */
    public org.joda.time.DateTime getDateTime( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDateTime();
    }

    public java.lang.String getAttachmentName()
        throws InconvertibleValueException
    {
        return value.asString();
    }

    public com.enonic.wem.api.content.binary.BinaryId getBinaryId()
        throws InconvertibleValueException
    {
        return value.asBinaryId();
    }

    /**
     * Returns the value at of the Property at the given array index as a BlobKey.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a BlobKey.
     */
    public com.enonic.wem.api.content.binary.BinaryId getBinaryId( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asBinaryId();
    }

    /**
     * Returns the value of the Property at the given array index.
     *
     * @see com.enonic.wem.api.data.Value#getData()
     */
    public com.enonic.wem.api.data.RootDataSet getData( final int arrayIndex )
        throws ClassCastException
    {
        return getArray().getValue( arrayIndex ).getData();
    }

    /**
     * @see com.enonic.wem.api.data.Value#getData()
     */
    public com.enonic.wem.api.data.RootDataSet getData()
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
    public java.lang.String toString()
    {
        final com.google.common.base.Objects.ToStringHelper s = com.google.common.base.Objects.toStringHelper( this );
        s.add( "name", getName() );
        s.add( "type", getValueType() );
        s.add( "value", value.getObject() );
        return s.toString();
    }

    public final static class ContentId
        extends Property
    {
        public ContentId( final java.lang.String name, final com.enonic.wem.api.content.ContentId value )
        {
            super( name, new Value.ContentId( value ) );
        }

        public ContentId( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        ContentId( final ContentId source )
        {
            super( source );
        }

        public ContentId copy()
        {
            return new ContentId( this );
        }
    }

    public final static class EntityId
        extends Property
    {
        public EntityId( final java.lang.String name, final com.enonic.wem.api.entity.EntityId value )
        {
            super( name, new Value.EntityId( value ) );
        }

        public EntityId( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        EntityId( final EntityId source )
        {
            super( source );
        }

        public EntityId copy()
        {
            return new EntityId( this );
        }
    }

    public final static class BinaryId
        extends Property
    {
        public BinaryId( final java.lang.String name, final com.enonic.wem.api.content.binary.BinaryId value )
        {
            super( name, new Value.BinaryId( value ) );
        }

        public BinaryId( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        BinaryId( final BinaryId source )
        {
            super( source );
        }

        public BinaryId copy()
        {
            return new BinaryId( this );
        }
    }

    public final static class AttachmentName
        extends Property
    {
        public AttachmentName( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.AttachmentName( value ) );
        }

        public AttachmentName( final AttachmentName source )
        {
            super( source );
        }

        public AttachmentName copy()
        {
            return new AttachmentName( this );
        }
    }

    public static class GeographicCoordinate
        extends Property
    {
        public GeographicCoordinate( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        public GeographicCoordinate( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.GeoPoint( value ) );
        }

        GeographicCoordinate( final GeographicCoordinate source )
        {
            super( source );
        }

        public GeographicCoordinate copy()
        {
            return new GeographicCoordinate( this );
        }
    }


    public final static class Date
        extends Property
    {
        public Date( final java.lang.String name, final org.joda.time.DateMidnight value )
        {
            super( name, new Value.DateMidnight( value ) );
        }

        public Date( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.DateMidnight( value ) );
        }

        public Date( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        Date( final Date source )
        {
            super( source );
        }

        public Date copy()
        {
            return new Date( this );
        }
    }

    public final static class Double
        extends Property
    {
        public Double( final java.lang.String name, final java.lang.Double value )
        {
            super( name, new Value.Double( value ) );
        }

        public Double( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        Double( final Double source )
        {
            super( source );
        }

        public Double copy()
        {
            return new Double( this );
        }
    }

    public static final class HtmlPart
        extends Property
    {
        public HtmlPart( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.HtmlPart( value ) );
        }

        public HtmlPart( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        HtmlPart( final HtmlPart source )
        {
            super( source );
        }

        public HtmlPart copy()
        {
            return new HtmlPart( this );
        }
    }

    public final static class String
        extends Property
    {
        public String( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.String( value ) );
        }

        public String( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        String( final String source )
        {
            super( source );
        }

        public String copy()
        {
            return new String( this );
        }

    }

    public final static class Long
        extends Property
    {
        public Long( final java.lang.String name, final java.lang.Long value )
        {
            super( name, new Value.Long( value ) );
        }

        public Long( final java.lang.String name, final Integer value )
        {
            super( name, new Value.Long( value ) );
        }

        public Long( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        Long( final Long source )
        {
            super( source );
        }

        public Long copy()
        {
            return new Long( this );
        }
    }

    public static final class Xml
        extends Property
    {
        public Xml( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.Xml( value ) );
        }

        public Xml( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        Xml( final Xml source )
        {
            super( source );
        }

        public Xml copy()
        {
            return new Xml( this );
        }
    }

    public static final class Data
        extends Property
    {
        public Data( final java.lang.String name, final com.enonic.wem.api.data.RootDataSet value )
        {
            super( name, new Value.Data( value ) );
        }

        public Data( final java.lang.String name, final Value value )
        {
            super( name, value );
        }

        Data( final Data source )
        {
            super( source );
        }

        public Data copy()
        {
            return new Data( this );
        }
    }
}
