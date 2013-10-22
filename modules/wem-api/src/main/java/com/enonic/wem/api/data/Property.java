package com.enonic.wem.api.data;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.type.InconvertibleValueException;
import com.enonic.wem.api.data.type.JavaTypeConverter;
import com.enonic.wem.api.data.type.ValueOfUnexpectedClassException;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.form.InvalidDataException;
import com.enonic.wem.api.form.InvalidValueException;

public class Property
    extends Data
{
    private Value value;

    Property( final Property source )
    {
        super( source );
        this.value = source.value;
    }

    Property( final java.lang.String name, final Value value )
    {
        super( name );
        Preconditions.checkNotNull( value, "value cannot be null" );
        this.value = value;
    }

    Property( final AbstractBaseBuilder builder )
    {
        super( builder.name );
        if ( builder.value == null )
        {
            this.value = builder.valueType.newValue( builder.rawValue );
        }
        else
        {
            this.value = builder.value;
        }

        try
        {
            getValueType().checkValidity( this );
        }
        catch ( ValueOfUnexpectedClassException e )
        {
            throw new InvalidDataException( this, e );
        }
        catch ( InvalidValueException e )
        {
            throw new InvalidDataException( this, e );
        }
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

    public void checkValueTypeValidity()
        throws InvalidDataException
    {
        try
        {
            getValueType().checkValidity( this );
        }
        catch ( ValueOfUnexpectedClassException e )
        {
            throw new InvalidDataException( this, e );
        }
        catch ( InvalidValueException e )
        {
            throw new InvalidDataException( this, e );
        }
    }

    @Override
    public Data copy()
    {
        return newProperty( this ).build();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Property property = (Property) o;

        return Objects.equal( getName(), property.getName() ) && Objects.equal( value, property.value );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( getName(), value );
    }

    @Override
    public java.lang.String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "name", getName() );
        s.add( "type", getValueType() );
        s.add( "value", value.getObject() );
        return s.toString();
    }

    public static Builder newProperty()
    {
        return new Builder();
    }

    public static Builder newProperty( Property property )
    {
        return new Builder( property );
    }

    public static TypeBuilder newProperty( final java.lang.String name )
    {
        return new NameBuilder().name( name );
    }

    public static class NameBuilder
    {
        private final Builder builder = new Builder();

        private NameBuilder()
        {

        }

        public TypeBuilder name( final java.lang.String name )
        {
            builder.name( name );
            return new TypeBuilder( builder );
        }
    }

    public static class TypeBuilder
    {
        private final Builder builder;

        private TypeBuilder( final Builder builder )
        {
            this.builder = builder;
        }

        public ValueBuilder type( ValueType value )
        {
            builder.type( value );
            return new ValueBuilder( builder );
        }
    }

    public static class ValueBuilder
    {
        private final Builder builder;

        private ValueBuilder( final Builder builder )
        {
            this.builder = builder;
        }

        public ValueBuilder value( Object value )
        {
            builder.value( value );
            return this;
        }

        public Property build()
        {
            return new Property( builder );
        }
    }

    public static class Builder
        extends AbstractNameBuilder<Builder>
    {

        public Builder()
        {

        }

        public Builder( final Property property )
        {
            this.name( property.getName() ).type( property.getValueType() ).value( property.getValue() );
        }

        public Builder type( ValueType value )
        {
            super.setType( value );
            return this;
        }

        @Override
        public Property build()
        {
            return new Property( this );
        }

        public Builder value( Object value )
        {
            if ( value instanceof Value )
            {
                super.setValue( (Value) value );
            }
            else
            {
                super.setValue( value );
            }
            return this;
        }
    }

    public abstract static class AbstractNameBuilder<T extends AbstractNameBuilder>
        extends AbstractBaseBuilder
    {
        public AbstractNameBuilder()
        {
        }

        @SuppressWarnings("unchecked")
        private T getThis()
        {
            return (T) this;
        }

        public T name( final java.lang.String value )
        {
            setName( value );
            return getThis();
        }

        public abstract Property build();
    }

    public abstract static class AbstractBaseBuilder
    {
        private java.lang.String name;

        private Value value;

        private Object rawValue;

        private ValueType valueType;

        AbstractBaseBuilder()
        {
        }

        void setName( final java.lang.String value )
        {
            this.name = value;
        }

        void setType( final ValueType value )
        {
            this.valueType = value;
        }

        void setValue( final Object value )
        {
            this.rawValue = value;
        }

        void setValue( final Value value )
        {
            this.value = value;
        }
    }

    public final static class ContentId
        extends Property
    {
        public ContentId( final java.lang.String name, final com.enonic.wem.api.content.ContentId value )
        {
            super( name, new Value.ContentId( value ) );
        }

        private ContentId( final AbstractBaseBuilder builder )
        {
            super( builder );
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

        public static Builder newContentId()
        {
            return new Builder();
        }

        public static class Builder
            extends AbstractNameBuilder<Builder>
        {
            public Builder()
            {
                setType( ValueTypes.CONTENT_ID );
            }

            public Builder value( final com.enonic.wem.api.content.ContentId value )
            {
                setValue( value );
                return this;
            }

            public Builder value( final java.lang.String value )
            {
                setValue( JavaTypeConverter.ContentId.GET.convertFromString( value ) );
                return this;
            }

            @Override
            public ContentId build()
            {
                return new ContentId( this );
            }
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

        private BinaryId( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        BinaryId( final BinaryId source )
        {
            super( source );
        }

        public BinaryId copy()
        {
            return new BinaryId( this );
        }

        public static Builder newBinaryId()
        {
            return new Builder();
        }


        public static class Builder
            extends AbstractNameBuilder<Builder>
        {
            public Builder()
            {
                setType( ValueTypes.BINARY_ID );
            }

            public Builder value( final com.enonic.wem.api.content.binary.BinaryId value )
            {
                setValue( value );
                return this;
            }

            public Builder value( final java.lang.String value )
            {
                setValue( JavaTypeConverter.BinaryId.GET.convertFromString( value ) );
                return this;
            }

            @Override
            public BinaryId build()
            {
                return new BinaryId( this );
            }
        }
    }

    public final static class AttachmentName
        extends Property
    {
        public AttachmentName( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.AttachmentName( value ) );
        }

        private AttachmentName( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public AttachmentName( final AttachmentName source )
        {
            super( source );
        }

        public AttachmentName copy()
        {
            return new AttachmentName( this );
        }

        public static Builder newAttachmentName()
        {
            return new Builder();
        }


        public static class Builder
            extends AbstractNameBuilder<Builder>
        {
            public Builder()
            {
                setType( ValueTypes.ATTACHMENT_NAME );
            }

            public Builder value( final java.lang.String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public AttachmentName build()
            {
                return new AttachmentName( this );
            }
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
            super( name, new Value.GeographicCoordinate( value ) );
        }

        public GeographicCoordinate( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        GeographicCoordinate( final GeographicCoordinate source )
        {
            super( source );
        }

        public GeographicCoordinate copy()
        {
            return new GeographicCoordinate( this );
        }

        public static GeographicCoordinateBuilder newGeographicCoordinate()
        {
            return new GeographicCoordinateBuilder();
        }

        public static class GeographicCoordinateBuilder
            extends AbstractNameBuilder<GeographicCoordinateBuilder>
        {
            public GeographicCoordinateBuilder()
            {
                setType( ValueTypes.GEOGRAPHIC_COORDINATE );
            }

            public GeographicCoordinateBuilder value( final java.lang.String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public GeographicCoordinate build()
            {
                return new GeographicCoordinate( this );
            }
        }

        public static GeographicCoordinateValueBuilder newGeographicCoordinate( final java.lang.String name )
        {
            return new GeographicCoordinateValueBuilder( name );
        }

        public static class GeographicCoordinateValueBuilder
            extends AbstractBaseBuilder
        {
            private GeographicCoordinateValueBuilder( final java.lang.String name )
            {
                setType( ValueTypes.GEOGRAPHIC_COORDINATE );
                setName( name );
            }

            public GeographicCoordinate value( final java.lang.String value )
            {
                setValue( value );
                return new GeographicCoordinate( this );
            }
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

        private Date( final AbstractBaseBuilder builder )
        {
            super( builder );
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

        public static DateBuilder newDate()
        {
            return new DateBuilder();
        }

        public static class DateBuilder
            extends AbstractNameBuilder<DateBuilder>
        {
            public DateBuilder()
            {
                setType( ValueTypes.DATE_MIDNIGHT );
            }

            public DateBuilder value( final org.joda.time.DateMidnight value )
            {
                setValue( value );
                return this;
            }

            public DateBuilder value( final java.lang.String value )
            {
                setValue( JavaTypeConverter.DateMidnight.GET.convertFromString( value ) );
                return this;
            }

            @Override
            public Date build()
            {
                return new Date( this );
            }
        }

        public static DateValueBuilder newDate( final java.lang.String name )
        {
            return new DateValueBuilder( name );
        }

        public static class DateValueBuilder
            extends AbstractBaseBuilder
        {
            private DateValueBuilder( final java.lang.String name )
            {
                setType( ValueTypes.DATE_MIDNIGHT );
                setName( name );
            }

            public Date value( final org.joda.time.DateMidnight value )
            {
                setValue( value );
                return new Date( this );
            }

            public Date value( final java.lang.String value )
            {
                setValue( JavaTypeConverter.DateMidnight.GET.convertFromString( value ) );
                return new Date( this );
            }
        }
    }

    public final static class Double
        extends Property
    {
        public Double( final java.lang.String name, final java.lang.Double value )
        {
            super( name, new Value.Double( value ) );
        }

        private Double( final AbstractBaseBuilder builder )
        {
            super( builder );
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

        public static DoubleBuilder newDouble()
        {
            return new DoubleBuilder();
        }

        public static class DoubleBuilder
            extends AbstractNameBuilder<DoubleBuilder>
        {
            private DoubleBuilder()
            {
                setType( ValueTypes.DOUBLE );
            }

            public DoubleBuilder( final java.lang.String name )
            {
                setType( ValueTypes.DOUBLE );
                setName( name );
            }

            public DoubleBuilder value( final java.lang.Double value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Double build()
            {
                return new Double( this );
            }
        }

        public static DoubleValueBuilder newDouble( final java.lang.String name )
        {
            return new DoubleValueBuilder( name );
        }

        public static class DoubleValueBuilder
            extends AbstractBaseBuilder
        {
            private DoubleValueBuilder( final java.lang.String name )
            {
                setType( ValueTypes.DOUBLE );
                setName( name );
            }

            public Double value( final java.lang.Double value )
            {
                setValue( value );
                return new Double( this );
            }
        }
    }

    public static final class HtmlPart
        extends Property
    {
        public HtmlPart( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.HtmlPart( value ) );
        }

        private HtmlPart( final AbstractBaseBuilder builder )
        {
            super( builder );
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

        public static HtmlPartBuilder newHtmlPart()
        {
            return new HtmlPartBuilder();
        }

        public static class HtmlPartBuilder
            extends AbstractNameBuilder<HtmlPartBuilder>
        {
            public HtmlPartBuilder()
            {
                setType( ValueTypes.HTML_PART );
            }

            public HtmlPartBuilder value( final java.lang.String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Property build()
            {
                return new HtmlPart( this );
            }
        }

        public static HtmlPartValueBuilder newHtmlPart( final java.lang.String name )
        {
            return new HtmlPartValueBuilder( name );
        }

        public static class HtmlPartValueBuilder
            extends AbstractBaseBuilder
        {
            private HtmlPartValueBuilder( final java.lang.String name )
            {
                setType( ValueTypes.HTML_PART );
                setName( name );
            }

            public HtmlPart value( final java.lang.String value )
            {
                setValue( value );
                return new HtmlPart( this );
            }
        }
    }

    public final static class String
        extends Property
    {
        public String( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.String( value ) );
        }

        private String( final AbstractBaseBuilder builder )
        {
            super( builder );
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

        public static StringBuilder newString()
        {
            return new StringBuilder();
        }

        public static class StringBuilder
            extends AbstractNameBuilder<StringBuilder>
        {
            public StringBuilder()
            {
                setType( ValueTypes.STRING );
            }

            public StringBuilder value( final java.lang.String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Property build()
            {
                return new String( this );
            }
        }

        public static StringValueBuilder newText( final java.lang.String name )
        {
            return new StringValueBuilder( name );
        }

        public static class StringValueBuilder
            extends AbstractBaseBuilder
        {
            private StringValueBuilder( final java.lang.String name )
            {
                setType( ValueTypes.STRING );
                setName( name );
            }

            public String value( final java.lang.String value )
            {
                setValue( value );
                return new String( this );
            }
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

        private Long( final AbstractBaseBuilder builder )
        {
            super( builder );
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

        public static LongBuilder newLong()
        {
            return new LongBuilder();
        }

        public static class LongBuilder
            extends AbstractNameBuilder<LongBuilder>
        {
            private LongBuilder()
            {
                setType( ValueTypes.LONG );
            }

            public LongBuilder value( final java.lang.Long value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Property build()
            {
                return new Long( this );
            }
        }

        public static LongValueBuilder newLong( final java.lang.String name )
        {
            return new LongValueBuilder( name );
        }

        public static class LongValueBuilder
            extends AbstractBaseBuilder
        {
            private LongValueBuilder( final java.lang.String name )
            {
                setType( ValueTypes.LONG );
                setName( name );
            }

            public Long value( final java.lang.Long value )
            {
                setValue( value );
                return new Long( this );
            }
        }
    }

    public static final class Xml
        extends Property
    {
        public Xml( final java.lang.String name, final java.lang.String value )
        {
            super( name, new Value.Xml( value ) );
        }

        private Xml( final AbstractBaseBuilder builder )
        {
            super( builder );
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

        public static XmlBuilder newXml()
        {
            return new XmlBuilder();
        }

        public static class XmlBuilder
            extends AbstractNameBuilder<XmlBuilder>
        {
            public XmlBuilder()
            {
                setType( ValueTypes.XML );
            }

            public XmlBuilder value( final java.lang.String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Property build()
            {
                return new Xml( this );
            }
        }

        public static XmlValueBuilder newXml( final java.lang.String name )
        {
            return new XmlValueBuilder( name );
        }

        public static class XmlValueBuilder
            extends AbstractBaseBuilder
        {
            private XmlValueBuilder( final java.lang.String name )
            {
                setType( ValueTypes.XML );
                setName( name );
            }

            public Xml value( final java.lang.String value )
            {
                setValue( value );
                return new Xml( this );
            }
        }
    }
}
