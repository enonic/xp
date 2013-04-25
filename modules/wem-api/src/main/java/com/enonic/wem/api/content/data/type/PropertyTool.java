package com.enonic.wem.api.content.data.type;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.schema.content.form.InvalidDataException;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public class PropertyTool
{
    /**
     * Throws InvalidDataException if the given data is a data set and not containing property at given path of given type.
     *
     * @param propertyContainingSet
     * @param path
     * @param valueType
     */
    public static void checkPropertyType( final Property propertyContainingSet, final String path, final ValueType valueType )
        throws InvalidDataException
    {
        final DataSet dataSet = propertyContainingSet.toDataSet();
        checkPropertyType( dataSet, path, valueType );
    }

    public static void checkPropertyType( final DataSet dataSet, final String path, final ValueType valueType )
        throws InvalidDataException
    {
        final Property property = dataSet.getProperty( EntryPath.from( path ) );
        if ( property == null )
        {
            return;
        }

        checkPropertyType( property, valueType );
    }

    public static void checkPropertyType( final Property property, final ValueType valueType )
        throws InvalidDataException
    {
        if ( !property.getType().equals( valueType ) )
        {
            throw new InvalidPropertyTypeException( property, valueType );
        }
    }

    public static void checkRequiredPath( final Property propertyContainingSet, String path )
    {
        final DataSet dataSet = propertyContainingSet.toDataSet();
        final Entry entry = dataSet.getEntry( EntryPath.from( path ) );
        if ( entry == null )
        {
            throw new InvalidDataException( propertyContainingSet, "entry required to have sub entry at path: " + path );
        }
    }

    public static void checkRange( final Property property, final Number rangeStart, final Number rangeStop )
        throws InvalidValueException
    {
        Preconditions.checkArgument( property.getType() == ValueTypes.WHOLE_NUMBER || property.getType() == ValueTypes.DECIMAL_NUMBER,
                                     "range checking can only be done for types: [" + ValueTypes.WHOLE_NUMBER + ", " +
                                         ValueTypes.DECIMAL_NUMBER + "]" );

        double value = property.getDouble();
        if ( value < rangeStart.doubleValue() || value > rangeStop.doubleValue() )
        {
            throw new InvalidValueException( property, "Value not within range from " + rangeStart + " to " + rangeStop );
        }
    }

    public static Checker newPropertyChecker()
    {
        return new Checker();
    }

    public static class Checker
    {
        private String path;

        private boolean pathRequired = false;

        private ValueType checkType;

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

        public Checker type( ValueType type )
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

        public void check( final Property property )
            throws InvalidValueException
        {
            Preconditions.checkNotNull( path, "path not set" );

            if ( checkType != null )
            {
                checkPropertyType( property, path, checkType );
            }
            if ( pathRequired )
            {
                checkRequiredPath( property, path );
            }
            if ( rangeStart != null && rangeStop != null )
            {
                final Property subProperty = property.toDataSet().getProperty( EntryPath.from( path ) );
                checkRange( subProperty, rangeStart, rangeStop );
            }
        }


    }

}
