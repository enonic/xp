package com.enonic.wem.api.content.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.JavaType;
import com.enonic.wem.api.content.schema.content.form.InvalidDataException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

import static com.enonic.wem.api.content.data.Value.newValue;

public class Data
    extends Entry
{
    private Value value;

    Data( final BaseBuilder builder )
    {
        super( builder.name );
        if ( builder.value == null )
        {
            builder.value = builder.valueBuilder.build();
        }
        this.value = builder.value;

        try
        {
            getType().checkValidity( this );
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

    public BaseDataType getType()
    {
        return value.getType();
    }

    public void setValue( final Object value, final BaseDataType dataType )
    {
        Preconditions.checkNotNull( value, "A Data cannot have a null value" );
        Preconditions.checkArgument( !( value instanceof DataSet ), "A Data cannot have a DataSet as value" );
        this.value = newValue().type( dataType ).value( value ).build();
    }

    public void setValue( final Value value )
    {
        Preconditions.checkNotNull( value, "A Data cannot have a null value" );
        this.value = value;
    }

    @Override
    public DataArray getArray()
    {
        return (DataArray) super.getArray();
    }

    public Value getValue()
    {
        return value;
    }

    public Value getValue( final int arrayIndex )
    {
        final DataArray array = getArray();
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

    /**
     * Returns the value at of the data at the given array index as a String.
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

    public Long getLong()
        throws InconvertibleValueException
    {
        return value.asLong();
    }

    /**
     * Returns the value at of the data at the given array index as a Long.
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
     * Returns the value at of the data at the given array index as a Double.
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
     * Returns the value at of the data at the given array index as a DateMidnight.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a org.joda.time.DateMidnight.
     */
    public DateMidnight getDate( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDate();
    }

    public BinaryId getBinaryId()
        throws InconvertibleValueException
    {
        return value.asBinaryId();
    }

    /**
     * Returns the value at of the data at the given array index as a BlobKey.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a BlobKey.
     */
    public BinaryId getBinaryId( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asBinaryId();
    }

    public void checkDataTypeValidity()
        throws InvalidDataException
    {
        try
        {
            getType().checkValidity( this );
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

        final Data data = (Data) o;

        return Objects.equal( getName(), data.getName() ) && Objects.equal( value, data.value );
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
        s.add( "type", getType() );
        s.add( "value", value.getObject() );
        return s.toString();
    }

    public static Builder newData()
    {
        return new Builder();
    }

    public static TypeBuilder newData( final String name )
    {
        return new NameBuilder().name( name );
    }

    public static Text.TextBuilder newText()
    {
        return new Text.TextBuilder();
    }

    public static WholeNumber.WholeNumberBuilder newWholeNumber()
    {
        return new WholeNumber.WholeNumberBuilder();
    }

    public static DecimalNumber.DecimalNumberBuilder newDecimalNumber()
    {
        return new DecimalNumber.DecimalNumberBuilder();
    }

    public static Date.DateBuilder newDate()
    {
        return new Date.DateBuilder();
    }

    public static ContentReference.Builder newContentReference()
    {
        return ContentReference.newContentReferenceBuilder();
    }

    public static HtmlPart.HtmlPartBuilder newHtmlPart()
    {
        return new HtmlPart.HtmlPartBuilder();
    }

    public static Xml.XmlBuilder newXml()
    {
        return new Xml.XmlBuilder();
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

        public ValueBuilder type( BaseDataType value )
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

        public Data build()
        {
            return new Data( builder );
        }
    }

    public static class Builder
        extends BaseBuilder<Builder>
    {
        public Builder type( BaseDataType value )
        {
            return super.setType( value );
        }

        @Override
        public Data build()
        {
            return new Data( this );
        }

        public Builder value( Object value )
        {
            if ( value instanceof Value )
            {
                super.setValue( (Value) value );
            }
            else if ( value instanceof Value.Builder )
            {
                super.setValue( ( (Value.Builder) value ).build() );
            }
            else
            {
                super.setValue( value );
            }
            return this;
        }
    }

    public abstract static class BaseBuilder<T extends BaseBuilder>
    {
        private String name;

        private Value.Builder valueBuilder = Value.newValue();

        private Value value;

        public BaseBuilder()
        {
        }

        @SuppressWarnings("unchecked")
        private T getThis()
        {
            return (T) this;
        }

        public T name( final String value )
        {
            this.name = value;
            return getThis();
        }

        T setType( final BaseDataType value )
        {
            this.valueBuilder.type( value );
            return getThis();
        }

        T setValue( final Object value )
        {
            this.valueBuilder.value( value );
            return getThis();
        }

        T setValue( final Value value )
        {
            this.value = value;
            return getThis();
        }

        public abstract Data build();
    }

    public final static class ContentReference
        extends Data
    {
        public ContentReference( final String name, final ContentId value )
        {
            super( newContentReferenceBuilder().name( name ).value( value ) );
        }

        ContentReference( final Builder builder )
        {
            super( builder );
        }

        public static Builder newContentReferenceBuilder()
        {
            return new Builder();
        }


        public static class Builder
            extends BaseBuilder<Builder>
        {
            public Builder()
            {
                setType( DataTypes.CONTENT_REFERENCE );
            }

            public Builder value( final ContentId value )
            {
                setValue( value );
                return this;
            }

            public Builder value( final String value )
            {
                setValue( JavaType.CONTENT_ID.convertFrom( value ) );
                return this;
            }

            @Override
            public ContentReference build()
            {
                return new ContentReference( this );
            }
        }
    }


    public final static class Date
        extends Data
    {
        public Date( final String name, final DateMidnight value )
        {
            super( newDate().name( name ).value( value ) );
        }

        public Date( final String name, final String value )
        {
            super( newDate().name( name ).value( value ) );
        }

        Date( final DateBuilder dateBuilder )
        {
            super( dateBuilder );
        }

        public static DateBuilder newDate()
        {
            return new DateBuilder();
        }

        public static class DateBuilder
            extends BaseBuilder<DateBuilder>
        {
            public DateBuilder()
            {
                setType( DataTypes.DATE_MIDNIGHT );
            }

            public DateBuilder value( final DateMidnight value )
            {
                setValue( value );
                return this;
            }

            public DateBuilder value( final String value )
            {
                setValue( JavaType.DATE_MIDNIGHT.convertFrom( value ) );
                return this;
            }

            @Override
            public Date build()
            {
                return new Date( this );
            }
        }
    }

    public final static class DecimalNumber
        extends Data
    {
        public DecimalNumber( final String name, final Double value )
        {
            super( newDecimalNumber().name( name ).value( value ) );
        }

        DecimalNumber( final DecimalNumberBuilder decimalNumberBuilder )
        {
            super( decimalNumberBuilder );
        }

        public static DecimalNumberBuilder newDecimalNumber()
        {
            return new DecimalNumberBuilder();
        }

        public static class DecimalNumberBuilder
            extends BaseBuilder<DecimalNumberBuilder>
        {
            public DecimalNumberBuilder()
            {
                setType( DataTypes.DECIMAL_NUMBER );
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
    }

    public static final class HtmlPart
        extends Data
    {
        public HtmlPart( final String name, final String value )
        {
            super( newHtmlPart().name( name ).value( value ) );
        }

        public HtmlPart( final HtmlPartBuilder builder )
        {
            super( builder );
        }

        public static HtmlPartBuilder newHtmlPart()
        {
            return new HtmlPartBuilder();
        }

        public static class HtmlPartBuilder
            extends BaseBuilder<HtmlPartBuilder>
        {
            public HtmlPartBuilder()
            {
                setType( DataTypes.HTML_PART );
            }

            public HtmlPartBuilder value( final String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Data build()
            {
                return new HtmlPart( this );
            }
        }
    }

    public final static class Text
        extends Data
    {
        public Text( final String name, final String value )
        {
            super( newText().name( name ).value( value ) );
        }

        public Text( final TextBuilder builder )
        {
            super( builder );
        }

        public static TextBuilder newText()
        {
            return new TextBuilder();
        }

        public static class TextBuilder
            extends BaseBuilder<TextBuilder>
        {
            public TextBuilder()
            {
                setType( DataTypes.TEXT );
            }

            public TextBuilder value( final String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Data build()
            {
                return new Text( this );
            }
        }
    }

    public final static class WholeNumber
        extends Data
    {
        public WholeNumber( final String name, final Long value )
        {
            super( newWholeNumber().name( name ).value( value ) );
        }

        public WholeNumber( final WholeNumberBuilder builder )
        {
            super( builder );
        }

        public static WholeNumberBuilder newWholeNumber()
        {
            return new WholeNumberBuilder();
        }

        public static class WholeNumberBuilder
            extends BaseBuilder<WholeNumberBuilder>
        {
            public WholeNumberBuilder()
            {
                setType( DataTypes.WHOLE_NUMBER );
            }

            public WholeNumberBuilder value( final Long value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Data build()
            {
                return new WholeNumber( this );
            }
        }
    }

    public static final class Xml
        extends Data
    {
        public Xml( final String name, final String value )
        {
            super( newXml().name( name ).value( value ) );
        }

        public Xml( final XmlBuilder builder )
        {
            super( builder );
        }

        public static XmlBuilder newXml()
        {
            return new XmlBuilder();
        }

        public static class XmlBuilder
            extends BaseBuilder<XmlBuilder>
        {
            public XmlBuilder()
            {
                setType( DataTypes.XML );
            }

            public XmlBuilder value( final String value )
            {
                setValue( value );
                return this;
            }

            @Override
            public Data build()
            {
                return new Xml( this );
            }
        }
    }
}
