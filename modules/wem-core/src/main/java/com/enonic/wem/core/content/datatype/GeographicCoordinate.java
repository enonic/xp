package com.enonic.wem.core.content.datatype;


import com.enonic.wem.core.content.data.Data;
import com.enonic.wem.core.content.type.formitem.InvalidValueException;
import com.enonic.wem.core.content.type.formitem.comptype.TypedPath;

public class GeographicCoordinate
    extends BaseDataType
{

    private static final String LATITUDE = "latitude";

    private static final String LONGITUDE = "longitude";

    GeographicCoordinate( int key )
    {
        super( key, JavaType.DATA_SET, TypedPath.newTypedPath( LATITUDE, DataTypes.DECIMAL_NUMBER ),
               TypedPath.newTypedPath( LONGITUDE, DataTypes.DECIMAL_NUMBER ) );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null;
    }

    @Override
    public void checkValidity( final Data data )
        throws InvalidValueTypeException
    {
        super.checkValidity( data );

        com.enonic.wem.core.content.data.DataSet dataSet = data.getDataSet();
        Data latitude = dataSet.getData( LATITUDE );
        if ( latitude != null )
        {
            Double latitudeAsDouble = (Double) latitude.getValue();
            if ( latitudeAsDouble < -90 || latitudeAsDouble > 90 )
            {
                throw new InvalidValueException( "A latitude is ranging from -90 to +90: " + latitudeAsDouble );
            }
        }

        Data longitude = dataSet.getData( LONGITUDE );
        if ( longitude != null )
        {
            Double longitudeAsDouble = (Double) longitude.getValue();
            if ( longitudeAsDouble < -180 || longitudeAsDouble > 180 )
            {
                throw new InvalidValueException( "A longitude is ranging from -180 to +180: " + longitudeAsDouble );
            }
        }
    }
}
