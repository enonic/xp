package com.enonic.wem.api.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.type.InconvertibleValueException;
import com.enonic.wem.api.data.type.InvalidValueTypeException;
import com.enonic.wem.api.data.type.JavaTypeConverter;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;
import com.enonic.wem.api.schema.content.form.InvalidDataException;
import com.enonic.wem.api.schema.content.form.InvalidValueException;

public class Property
    extends Data
{
    private Value value;

    Property( final String name, final Value value )
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
        catch ( InvalidValueTypeException e )
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

    public String getString()
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
    public String getString( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asString();
    }

    public com.enonic.wem.api.content.ContentId getContentId( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asContentId();
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

    public DateMidnight getDate()
        throws InconvertibleValueException
    {
        return value.asDate();
    }

    /**
     * Returns the value at of the Property at the given array index as a DateMidnight.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a org.joda.time.DateMidnight.
     */
    public DateMidnight getDate( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDate();
    }

    public String getAttachmentName()
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
        catch ( InvalidValueTypeException e )
        {
            throw new InvalidDataException( this, e );
        }
        catch ( InvalidValueException e )
        {
            throw new InvalidDataException( this, e );
        }
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
    public String toString()
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

    public static TypeBuilder newProperty( final String name )
    {
        return new NameBuilder().name( name );
    }

    public static class NameBuilder
    {
        private final Builder builder = new Builder();

        private NameBuilder()
        {

        }

        public TypeBuilder name( final String name )
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

        public T name( final String value )
        {
            setName( value );
            return getThis();
        }

        public abstract Property build();
    }

    public abstract static class AbstractBaseBuilder
    {
        private String name;

        private Value value;

        private Object rawValue;

        private ValueType valueType;

        AbstractBaseBuilder()
        {
        }

        void setName( final String value )
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
        public ContentId( final String name, final com.enonic.wem.api.content.ContentId value )
        {
            super( name, new Value.ContentId( value ) );
        }

        private ContentId( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public ContentId( final String name, final Value value )
        {
            super( name, value );
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

            public Builder value( final String value )
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
        public BinaryId( final String name, final com.enonic.wem.api.content.binary.BinaryId value )
        {
            super( name, new Value.BinaryId( value ) );
        }

        public BinaryId( final String name, final Value value )
        {
            super( name, value );
        }

        private BinaryId( final AbstractBaseBuilder builder )
        {
            super( builder );
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

            public Builder value( final String value )
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
        public AttachmentName( final String name, final String value )
        {
            super( name, new Value.AttachmentName( value ) );
        }

        private AttachmentName( final AbstractBaseBuilder builder )
        {
            super( builder );
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

            public Builder value( final String value )
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
        public GeographicCoordinate( final String name, final Value value )
        {
            super( name, value );
        }

        public GeographicCoordinate( final String name, final String value )
        {
            super( name, new Value.GeographicCoordinate( value ) );
        }

        public GeographicCoordinate( final AbstractBaseBuilder builder )
        {
            super( builder );
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

            public GeographicCoordinateBuilder value( final String value )
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

        public static GeographicCoordinateValueBuilder newGeographicCoordinate( final String name )
        {
            return new GeographicCoordinateValueBuilder( name );
        }

        public static class GeographicCoordinateValueBuilder
            extends AbstractBaseBuilder
        {
            private GeographicCoordinateValueBuilder( final String name )
            {
                setType( ValueTypes.GEOGRAPHIC_COORDINATE );
                setName( name );
            }

            public GeographicCoordinate value( final String value )
            {
                setValue( value );
                return new GeographicCoordinate( this );
            }
        }
    }


    public final static class Date
        extends Property
    {
        public Date( final String name, final DateMidnight value )
        {
            super( name, new Value.Date( value ) );
        }

        public Date( final String name, final String value )
        {
            super( name, new Value.Date( value ) );
        }

        private Date( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public Date( final String name, final Value value )
        {
            super( name, value );
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

            public DateBuilder value( final DateMidnight value )
            {
                setValue( value );
                return this;
            }

            public DateBuilder value( final String value )
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

        public static DateValueBuilder newDate( final String name )
        {
            return new DateValueBuilder( name );
        }

        public static class DateValueBuilder
            extends AbstractBaseBuilder
        {
            private DateValueBuilder( final String name )
            {
                setType( ValueTypes.DATE_MIDNIGHT );
                setName( name );
            }

            public Date value( final DateMidnight value )
            {
                setValue( value );
                return new Date( this );
            }

            public Date value( final String value )
            {
                setValue( JavaTypeConverter.DateMidnight.GET.convertFromString( value ) );
                return new Date( this );
            }
        }
    }

    public final static class DecimalNumber
        extends Property
    {
        public DecimalNumber( final String name, final Double value )
        {
            super( name, new Value.DecimalNumber( value ) );
        }

        private DecimalNumber( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public DecimalNumber( final String name, final Value value )
        {
            super( name, value );
        }

        public static DecimalNumberBuilder newDecimalNumber()
        {
            return new DecimalNumberBuilder();
        }

        public static class DecimalNumberBuilder
            extends AbstractNameBuilder<DecimalNumberBuilder>
        {
            private DecimalNumberBuilder()
            {
                setType( ValueTypes.DECIMAL_NUMBER );
            }

            public DecimalNumberBuilder( final String name )
            {
                setType( ValueTypes.DECIMAL_NUMBER );
                setName( name );
            }

            public DecimalNumberBuilder value( final Double value )
            {
                setValue( value );
                return this;
            }

            @Override
            public DecimalNumber build()
            {
                return new DecimalNumber( this );
            }
        }

        public static DecimalNumberValueBuilder newDecimalNumber( final String name )
        {
            return new DecimalNumberValueBuilder( name );
        }

        public static class DecimalNumberValueBuilder
            extends AbstractBaseBuilder
        {
            private DecimalNumberValueBuilder( final String name )
            {
                setType( ValueTypes.DECIMAL_NUMBER );
                setName( name );
            }

            public DecimalNumber value( final Double value )
            {
                setValue( value );
                return new DecimalNumber( this );
            }
        }
    }

    public static final class HtmlPart
        extends Property
    {
        public HtmlPart( final String name, final String value )
        {
            super( name, new Value.HtmlPart( value ) );
        }

        private HtmlPart( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public HtmlPart( final String name, final Value value )
        {
            super( name, value );
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

            public HtmlPartBuilder value( final String value )
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

        public static HtmlPartValueBuilder newHtmlPart( final String name )
        {
            return new HtmlPartValueBuilder( name );
        }

        public static class HtmlPartValueBuilder
            extends AbstractBaseBuilder
        {
            private HtmlPartValueBuilder( final String name )
            {
                setType( ValueTypes.HTML_PART );
                setName( name );
            }

            public HtmlPart value( final String value )
            {
                setValue( value );
                return new HtmlPart( this );
            }
        }
    }

    public final static class Text
        extends Property
    {
        public Text( final String name, final String value )
        {
            super( name, new Value.Text( value ) );
        }

        private Text( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public Text( final String name, final Value value )
        {
            super( name, value );
        }

        public static TextBuilder newText()
        {
            return new TextBuilder();
        }

        public static class TextBuilder
            extends AbstractNameBuilder<TextBuilder>
        {
            public TextBuilder()
            {
                setType( ValueTypes.TEXT );
            }

            public TextBuilder value( final String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Property build()
            {
                return new Text( this );
            }
        }

        public static TextValueBuilder newText( final String name )
        {
            return new TextValueBuilder( name );
        }

        public static class TextValueBuilder
            extends AbstractBaseBuilder
        {
            private TextValueBuilder( final String name )
            {
                setType( ValueTypes.TEXT );
                setName( name );
            }

            public Text value( final String value )
            {
                setValue( value );
                return new Text( this );
            }
        }
    }

    public final static class WholeNumber
        extends Property
    {
        public WholeNumber( final String name, final Long value )
        {
            super( name, new Value.WholeNumber( value ) );
        }

        private WholeNumber( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public WholeNumber( final String name, final Value value )
        {
            super( name, value );
        }

        public static WholeNumberBuilder newWholeNumber()
        {
            return new WholeNumberBuilder();
        }

        public static class WholeNumberBuilder
            extends AbstractNameBuilder<WholeNumberBuilder>
        {
            private WholeNumberBuilder()
            {
                setType( ValueTypes.WHOLE_NUMBER );
            }

            public WholeNumberBuilder value( final Long value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Property build()
            {
                return new WholeNumber( this );
            }
        }

        public static WholeNumberValueBuilder newWholeNumber( final String name )
        {
            return new WholeNumberValueBuilder( name );
        }

        public static class WholeNumberValueBuilder
            extends AbstractBaseBuilder
        {
            private WholeNumberValueBuilder( final String name )
            {
                setType( ValueTypes.WHOLE_NUMBER );
                setName( name );
            }

            public WholeNumber value( final Long value )
            {
                setValue( value );
                return new WholeNumber( this );
            }
        }
    }

    public static final class Xml
        extends Property
    {
        public Xml( final String name, final String value )
        {
            super( name, new Value.Xml( value ) );
        }

        private Xml( final AbstractBaseBuilder builder )
        {
            super( builder );
        }

        public Xml( final String name, final Value value )
        {
            super( name, value );
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

            public XmlBuilder value( final String value )
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

        public static XmlValueBuilder newXml( final String name )
        {
            return new XmlValueBuilder( name );
        }

        public static class XmlValueBuilder
            extends AbstractBaseBuilder
        {
            private XmlValueBuilder( final String name )
            {
                setType( ValueTypes.XML );
                setName( name );
            }

            public Xml value( final String value )
            {
                setValue( value );
                return new Xml( this );
            }
        }
    }
}
