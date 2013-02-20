package com.enonic.wem.api.content.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InconvertibleValueException;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
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
        this.value = builder.value;
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

    public String asString()
        throws InconvertibleValueException
    {
        return value.asString();
    }

    /**
     * Returns the value at of the data at the given array index as a String.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a String.
     */
    public String asString( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asString();
    }

    public Long asLong()
        throws InconvertibleValueException
    {
        return value.asLong();
    }

    /**
     * Returns the value at of the data at the given array index as a Long.
     *
     * @throws InconvertibleValueException if the value is of another type and cannot not be converted to a Long.
     */
    public Long asLong( final int arrayIndex )
        throws InconvertibleValueException
    {
        return getArray().getValue( arrayIndex ).asLong();
    }

    public Double asDouble()
        throws InconvertibleValueException
    {
        return value.asDouble();
    }

    public DateMidnight asDate()
        throws InconvertibleValueException
    {
        return value.asDate();
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
        TextBuilder builder = new TextBuilder();
        builder.type( DataTypes.TEXT );
        return builder;
    }

    public static XmlBuilder newXml()
    {
        XmlBuilder builder = new XmlBuilder();
        builder.type( DataTypes.XML );
        return builder;
    }

    public static class Builder
        extends BaseBuilder<Builder>
    {
        public Builder type( BaseDataType value )
        {
            return super.type( value );
        }
    }

    public static class TextBuilder
        extends BaseBuilder
    {
        public TextBuilder()
        {
            valueBuilder.type( DataTypes.TEXT );
        }
    }

    public static class XmlBuilder
        extends BaseBuilder
    {
        public XmlBuilder()
        {
            valueBuilder.type( DataTypes.XML );
        }
    }

    public static class BaseBuilder<T extends BaseBuilder>
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

        public Data build()
        {
            if ( value == null )
            {
                value = valueBuilder.build();
            }

            final Data data = new Data( this );
            data.getType().ensureType( data );

            try
            {
                data.getType().checkValidity( data );
            }
            catch ( InvalidValueTypeException e )
            {
                throw new InvalidDataException( data, e );
            }
            catch ( InvalidValueException e )
            {
                throw new InvalidDataException( data, e );
            }

            return data;
        }
    }
}
