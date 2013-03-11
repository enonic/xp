package com.enonic.wem.api.content.data.type;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.schema.content.form.InvalidValueException;

public class GeographicCoordinate
    extends BaseDataType
{
    private static final String LATITUDE = "latitude";

    private static final String LONGITUDE = "longitude";

    GeographicCoordinate( int key )
    {
        super( key, JavaType.DATA_SET );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null;
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException, InvalidValueException
    {
        DataTool.newDataChecker().pathRequired( LATITUDE ).range( -90, 90 ).type( DataTypes.DECIMAL_NUMBER ).check( data );
        DataTool.newDataChecker().pathRequired( LONGITUDE ).range( -180, 180 ).type( DataTypes.DECIMAL_NUMBER ).check( data );
    }

    @Override
    public boolean hasCorrectType( final Object value )
    {
        if ( DataSet.class.isInstance( value ) )
        {
            final DataSet dataSet = (DataSet) value;
            final Data latitude = dataSet.getData( LATITUDE );
            if ( latitude == null )
            {
                return false;
            }
            final Data longitude = dataSet.getData( LONGITUDE );
            if ( longitude == null )
            {
                return false;
            }
            if ( DataTypes.DECIMAL_NUMBER.hasCorrectType( latitude.getObject() ) &&
                DataTypes.DECIMAL_NUMBER.hasCorrectType( longitude.getObject() ) )
            {
                return true;
            }
        }

        return false;

    }

    private Value toGeographicalCoordinate( final Value value )
    {
        if ( hasCorrectType( value ) )
        {
            return value;
        }
        /*else if ( value instanceof DataSet )
        {
            DataSet dataSet = (DataSet) value;
            DataTool.ensureType( DataTypes.DECIMAL_NUMBER, dataSet.getData( LATITUDE ) );
            DataTool.ensureType( DataTypes.DECIMAL_NUMBER, dataSet.getData( LONGITUDE ) );
            return dataSet;
        }*/
        else
        {
            throw new InconvertibleValueException( value, this );
        }

    }
}
