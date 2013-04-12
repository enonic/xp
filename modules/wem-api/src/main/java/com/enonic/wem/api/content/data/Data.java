package com.enonic.wem.api.content.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
import com.enonic.wem.api.content.data.type.JavaType;
import com.enonic.wem.api.content.schema.content.form.InvalidDataException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public class Data
    extends Entry
{
    private Value value;

    Data( final String name, final Value value )
    {
        super( name );
        Preconditions.checkNotNull( value, "value cannot be null" );
        this.value = value;
    }

    Data( final AbstractBaseBuilder builder )
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
        this.value = dataType.newValue( value );
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

    public com.enonic.wem.api.content.ContentId getContentId()
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

    public com.enonic.wem.api.content.binary.BinaryId getBinaryId()
        throws InconvertibleValueException
    {
        return value.asBinaryId();
    }

    /**
     * Returns the value at of the data at the given array index as a BlobKey.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a BlobKey.
     */
    public com.enonic.wem.api.content.binary.BinaryId getBinaryId( final int arrayIndex )
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
        extends AbstractNameBuilder<Builder>
    {
        public Builder type( BaseDataType value )
        {
            super.setType( value );
            return this;
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

        public abstract Data build();
    }

    public abstract static class AbstractBaseBuilder
    {
        private String name;

        private Value.Builder valueBuilder = Value.newValue();

        private Value value;

        AbstractBaseBuilder()
        {
        }

        void setName( final String value )
        {
            this.name = value;
        }

        void setType( final BaseDataType value )
        {
            this.valueBuilder.type( value );
        }

        void setValue( final Object value )
        {
            this.valueBuilder.value( value );
        }

        void setValue( final Value value )
        {
            this.value = value;
        }
    }

    public final static class ContentId
        extends Data
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
                setType( DataTypes.CONTENT_ID );
            }

            public Builder value( final com.enonic.wem.api.content.ContentId value )
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
            public ContentId build()
            {
                return new ContentId( this );
            }
        }
    }

    public final static class BinaryId
        extends Data
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
                setType( DataTypes.BINARY_ID );
            }

            public Builder value( final com.enonic.wem.api.content.binary.BinaryId value )
            {
                setValue( value );
                return this;
            }

            public Builder value( final String value )
            {
                setValue( JavaType.BINARY_ID.convertFrom( value ) );
                return this;
            }

            @Override
            public BinaryId build()
            {
                return new BinaryId( this );
            }
        }
    }


    public final static class Date
        extends Data
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

        public static DateValueBuilder newDate( final String name )
        {
            return new DateValueBuilder( name );
        }

        public static class DateValueBuilder
            extends AbstractBaseBuilder
        {
            private DateValueBuilder( final String name )
            {
                setType( DataTypes.DATE_MIDNIGHT );
                setName( name );
            }

            public Date value( final DateMidnight value )
            {
                setValue( value );
                return new Date( this );
            }

            public Date value( final String value )
            {
                setValue( JavaType.DATE_MIDNIGHT.convertFrom( value ) );
                return new Date( this );
            }
        }
    }

    public final static class DecimalNumber
        extends Data
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
                setType( DataTypes.DECIMAL_NUMBER );
            }

            public DecimalNumberBuilder( final String name )
            {
                setType( DataTypes.DECIMAL_NUMBER );
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
                setType( DataTypes.DECIMAL_NUMBER );
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
        extends Data
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

        public static HtmlPartValueBuilder newHtmlPart( final String name )
        {
            return new HtmlPartValueBuilder( name );
        }

        public static class HtmlPartValueBuilder
            extends AbstractBaseBuilder
        {
            private HtmlPartValueBuilder( final String name )
            {
                setType( DataTypes.HTML_PART );
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
        extends Data
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

        public static TextValueBuilder newText( final String name )
        {
            return new TextValueBuilder( name );
        }

        public static class TextValueBuilder
            extends AbstractBaseBuilder
        {
            private TextValueBuilder( final String name )
            {
                setType( DataTypes.TEXT );
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
        extends Data
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

        public static WholeNumberValueBuilder newWholeNumber( final String name )
        {
            return new WholeNumberValueBuilder( name );
        }

        public static class WholeNumberValueBuilder
            extends AbstractBaseBuilder
        {
            private WholeNumberValueBuilder( final String name )
            {
                setType( DataTypes.WHOLE_NUMBER );
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
        extends Data
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


        public static XmlValueBuilder newXml( final String name )
        {
            return new XmlValueBuilder( name );
        }

        public static class XmlValueBuilder
            extends AbstractBaseBuilder
        {
            private XmlValueBuilder( final String name )
            {
                setType( DataTypes.XML );
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
