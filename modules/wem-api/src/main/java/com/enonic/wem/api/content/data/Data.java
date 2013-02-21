package com.enonic.wem.api.content.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.DataTypes;
import com.enonic.wem.api.content.data.type.InconvertibleValueException;
import com.enonic.wem.api.content.data.type.InvalidValueTypeException;
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

        final BaseDataType type = value.getType();
        type.ensureType( this );

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
     * Returns the value at of the data at the given array index as a Double.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a org.joda.time.DateMidnight.
     */
    public DateMidnight getDate( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asDate();
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

    public static TextBuilder newText()
    {
        return new TextBuilder();
    }

    public static WholeNumberBuilder newWholeNumber()
    {
        return new WholeNumberBuilder();
    }

    public static DecimalNumberBuilder newDecimalNumber()
    {
        return new DecimalNumberBuilder();
    }

    public static DateBuilder newDate()
    {
        return new DateBuilder();
    }

    public static HtmlPartBuilder newHtmlPart()
    {
        return new HtmlPartBuilder();
    }

    public static XmlBuilder newXml()
    {
        return new XmlBuilder();
    }

    public static class Builder
        extends BaseBuilder<Builder>
    {
        public Builder type( BaseDataType value )
        {
            return super.type( value );
        }

        @Override
        public Data build()
        {
            return new Data( this );
        }
    }

    public static class TextBuilder
        extends BaseBuilder
    {
        public TextBuilder()
        {
            valueBuilder.type( DataTypes.TEXT );
        }

        @Override
        public Data build()
        {
            return new Text( this );
        }
    }

    public static class WholeNumberBuilder
        extends BaseBuilder
    {
        public WholeNumberBuilder()
        {
            valueBuilder.type( DataTypes.WHOLE_NUMBER );
        }

        @Override
        public Data build()
        {
            return new WholeNumber( this );
        }
    }

    public static class DecimalNumberBuilder
        extends BaseBuilder
    {
        public DecimalNumberBuilder()
        {
            valueBuilder.type( DataTypes.DECIMAL_NUMBER );
        }

        @Override
        public DecimalNumber build()
        {
            return new DecimalNumber( this );
        }
    }

    public static class DateBuilder
        extends BaseBuilder
    {
        public DateBuilder()
        {
            valueBuilder.type( DataTypes.DATE );
        }

        @Override
        public Data build()
        {
            return new Date( this );
        }
    }

    public static class HtmlPartBuilder
        extends BaseBuilder
    {
        public HtmlPartBuilder()
        {
            valueBuilder.type( DataTypes.HTML_PART );
        }

        @Override
        public Data build()
        {
            return new HtmlPart( this );
        }
    }

    public static class XmlBuilder
        extends BaseBuilder
    {
        public XmlBuilder()
        {
            valueBuilder.type( DataTypes.XML );
        }

        @Override
        public Data build()
        {
            return new Xml( this );
        }
    }

    public abstract static class BaseBuilder<T extends BaseBuilder>
    {
        private String name;

        Value.Builder valueBuilder = Value.newValue();

        private Value value;

        public BaseBuilder()
        {
        }

        @SuppressWarnings("unchecked")
        private T getThis()
        {
            return (T) this;
        }

        public T name( String value )
        {
            this.name = value;
            return getThis();
        }

        T type( BaseDataType value )
        {
            this.valueBuilder.type( value );
            return getThis();
        }

        public T value( Object value )
        {
            if ( value instanceof Value )
            {
                this.value = (Value) value;
            }
            else if ( value instanceof Value.Builder )
            {
                this.value = ( (Value.Builder) value ).build();
            }
            else
            {
                this.valueBuilder.value( value );
            }
            return getThis();
        }

        public abstract Data build();
    }
}
