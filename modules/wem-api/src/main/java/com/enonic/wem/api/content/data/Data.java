package com.enonic.wem.api.content.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InconvertibleValueException;
import com.enonic.wem.api.content.datatype.InvalidValueTypeException;
import com.enonic.wem.api.content.datatype.JavaType;
import com.enonic.wem.api.content.type.component.InvalidDataException;
import com.enonic.wem.api.content.type.component.InvalidValueException;


public final class Data
{
    private EntryPath path;

    private Object value;

    private BaseDataType type;

    private Data()
    {
        // protection
    }

    /**
     * Sets the given index on this Data's path if it matches with the given.
     * If this Data's has a DataSet as value the same will be issued to the entries there.
     */
    void setEntryPathIndex( final EntryPath path, final int index )
    {
        this.path = this.path.asNewWithIndexAtPath( index, path );
        if ( type == DataTypes.SET )
        {
            getDataSet().setEntryPathIndex( path, index );
        }
    }

    public EntryPath getPath()
    {
        return path;
    }

    public DataType getDataType()
    {
        return type;
    }

    public void setValue( Object value )
    {
        this.value = value;
    }

    public boolean hasValue()
    {
        return this.value != null;
    }

    public boolean hasDataSetAsValue()
    {
        return type.equals( DataTypes.SET );
    }

    public boolean hasArrayAsValue()
    {
        return type.equals( DataTypes.ARRAY );
    }

    public Object getValue()
    {
        return value;
    }

    public String getString()
        throws InconvertibleValueException
    {
        final String converted = JavaType.STRING.convertFrom( value );
        if ( value != null && converted == null )
        {
            throw new InconvertibleValueException( value, JavaType.STRING );
        }
        return converted;
    }

    public Long getLong()
        throws InconvertibleValueException
    {
        final Long converted = JavaType.LONG.convertFrom( value );
        if ( value != null && converted == null )
        {
            throw new InconvertibleValueException( value, JavaType.LONG );
        }
        return converted;
    }

    public Double getDouble()
        throws InconvertibleValueException
    {
        final Double converted = JavaType.DOUBLE.convertFrom( value );
        if ( value != null && converted == null )
        {
            throw new InconvertibleValueException( value, JavaType.DOUBLE );
        }
        return converted;
    }

    public DateMidnight getDate()
        throws InconvertibleValueException
    {
        final DateMidnight converted = JavaType.DATE.convertFrom( value );
        if ( value != null && converted == null )
        {
            throw new InconvertibleValueException( value, JavaType.DATE );
        }
        return converted;
    }

    public DataSet getDataSet()
        throws InconvertibleValueException
    {
        final DataSet converted = JavaType.DATA_SET.convertFrom( value );
        if ( value != null && converted == null )
        {
            throw new InconvertibleValueException( value, JavaType.DATA_SET );
        }
        return converted;
    }

    public DataArray getDataArray()
        throws InconvertibleValueException
    {
        final DataArray converted = JavaType.DATA_ARRAY.convertFrom( value );
        if ( value != null && converted == null )
        {
            throw new InconvertibleValueException( value, JavaType.DATA_ARRAY );
        }
        return converted;
    }

    public void checkDataTypeValidity()
        throws InvalidDataException
    {
        try
        {
            type.checkValidity( this );
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

    public DataSet getDataSet( final EntryPath path )
    {
        Preconditions.checkArgument( getDataType().equals( DataTypes.SET ) );

        final DataSet dataSet = (DataSet) getValue();
        return dataSet.getDataSet( path );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "path", path );
        s.add( "type", type.getName() );
        s.add( "value", value );
        return s.toString();
    }

    public static Builder newData()
    {
        return new Builder();
    }

    public static class Builder
    {
        private EntryPath path;

        private Object value;

        private BaseDataType type;


        public Builder()
        {
            value = new Data();
        }

        public Builder path( EntryPath value )
        {
            this.path = value;
            return this;
        }

        public Builder type( DataType value )
        {
            this.type = (BaseDataType) value;
            return this;
        }

        public Builder value( Object value )
        {
            this.value = value;
            return this;
        }

        public Data build()
        {
            Preconditions.checkNotNull( this.type, "type is required" );

            final Data data = new Data();
            data.path = this.path;
            data.type = this.type;
            data.value = this.value;
            data.type.ensureType( data );

            try
            {
                data.type.checkValidity( data );
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
