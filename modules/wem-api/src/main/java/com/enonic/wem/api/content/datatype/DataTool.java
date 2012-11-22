package com.enonic.wem.api.content.datatype;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.type.form.InvalidDataException;
import com.enonic.wem.api.content.type.form.InvalidValueException;

public class DataTool
{
    /**
     * Throws InvalidDataException if the given data is a data set and not containing data at given path of given type.
     *
     * @param dataContainingSet
     * @param path
     * @param dataType
     */
    public static void checkDataType( final Data dataContainingSet, final String path, final DataType dataType )
        throws InvalidDataException
    {
        final DataSet dataSet = dataContainingSet.getDataSet();
        checkDataType( dataSet, path, dataType );
    }

    public static void checkDataType( final DataSet dataSet, final String path, final DataType dataType )
        throws InvalidDataException
    {
        final Data data = dataSet.getData( new EntryPath( path ) );
        if ( data == null )
        {
            return;
        }

        checkDataType( data, dataType );
    }

    public static void checkDataType( final Data data, final DataType dataType )
        throws InvalidDataException
    {
        if ( !data.getDataType().equals( dataType ) )
        {
            throw new InvalidDataTypeException( data, dataType );
        }
    }

    public static void checkRequiredPath( final Data dataContainingSet, String path )
    {
        final DataSet dataSet = dataContainingSet.getDataSet();
        final Data data = dataSet.getData( new EntryPath( path ) );
        if ( data == null )
        {
            throw new InvalidDataException( dataContainingSet, "data required to have sub data at path: " + path );
        }
    }

    public static void checkRange( final Data data, final Number rangeStart, final Number rangeStop )
        throws InvalidValueException
    {
        Preconditions.checkArgument( data.getDataType() == DataTypes.WHOLE_NUMBER || data.getDataType() == DataTypes.DECIMAL_NUMBER,
                                     "range checking can only be done for types: [" + DataTypes.WHOLE_NUMBER + ", " +
                                         DataTypes.DECIMAL_NUMBER + "]" );

        double value = data.getDouble();
        if ( value < rangeStart.doubleValue() || value > rangeStop.doubleValue() )
        {
            throw new InvalidValueException( data, "Value not within range from " + rangeStart + " to " + rangeStop );
        }
    }

    public static void ensureType( final DataType dataType, final Data data )
    {
        if ( data.getDataType().equals( dataType ) )
        {
            return;
        }

        dataType.ensureType( data );
    }

    public static Checker newDataChecker()
    {
        return new Checker();
    }

    public static class Checker
    {
        private String path;

        private boolean pathRequired = false;

        private DataType checkType;

        private Number rangeStart;

        private Number rangeStop;

        public Checker path( String path )
        {
            this.path = path;
            return this;
        }

        public Checker pathRequired( String path )
        {
            this.path = path;
            this.pathRequired = true;
            return this;
        }

        public Checker type( DataType type )
        {
            this.checkType = type;
            return this;
        }

        public Checker range( Number start, Number stop )
        {
            this.rangeStart = start;
            this.rangeStop = stop;
            return this;
        }

        public void check( final Data data )
            throws InvalidValueException
        {
            Preconditions.checkNotNull( path, "path not set" );

            if ( checkType != null )
            {
                checkDataType( data, path, checkType );
            }
            if ( pathRequired )
            {
                checkRequiredPath( data, path );
            }
            if ( rangeStart != null && rangeStop != null )
            {
                DataSet dataSet = data.getDataSet();
                Data subData = dataSet.getData( new EntryPath( path ) );
                checkRange( subData, rangeStart, rangeStop );
            }
        }


    }

}
