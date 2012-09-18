package com.enonic.wem.core.content.data;

import org.joda.time.DateMidnight;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.datatype.BaseDataType;
import com.enonic.wem.core.content.datatype.DataType;
import com.enonic.wem.core.content.datatype.DataTypes;
import com.enonic.wem.core.content.datatype.InvalidValueTypeException;
import com.enonic.wem.core.content.datatype.JavaType;
import com.enonic.wem.core.content.type.formitem.InvalidDataException;
import com.enonic.wem.core.content.type.formitem.InvalidValueException;


public class Data
{
    private EntryPath path;

    private Object value;

    private BaseDataType type;

    private Data()
    {
        // protection
    }

    public EntryPath getPath()
    {
        return path;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue( Object value )
    {
        this.value = value;
    }

    public String getString()
    {
        if ( type.isConvertibleTo( JavaType.STRING ) )
        {
            return type.convertToString( value );
        }
        return null;
    }

    public DateMidnight getDate()
    {
        //return DataTypes.DATE.toDate( this );
        return JavaType.DATE.toDate( this );
    }

    public DataType getDataType()
    {
        return type;
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

    public void setData( final EntryPath path, final Object value, final BaseDataType dataType )
    {
        Preconditions.checkArgument( type == DataTypes.DATA_SET, "TODO" );
        DataSet dataSet = (DataSet) this.value;
        dataSet.setData( path, value, dataType );
    }

    public DataSet getDataSet( final EntryPath path )
    {
        Preconditions.checkArgument( getDataType().equals( DataTypes.DATA_SET ) );

        final DataSet dataSet = (DataSet) getValue();
        return dataSet.getDataSet( path );
    }

    public boolean hasDataSetAsValue()
    {
        return type.equals( DataTypes.DATA_SET );
    }

    public DataSet getDataSet()
    {
        return (DataSet) value;
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

            return data;
        }
    }
}
