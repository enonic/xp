package com.enonic.wem.api.content.datatype;


import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.type.formitem.InvalidValueException;
import com.enonic.wem.api.content.type.formitem.inputtype.TypedPath;

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
        throws InvalidValueTypeException, InvalidValueException
    {
        super.checkValidity( data );

        DataTool.newDataChecker().pathRequired( LATITUDE ).range( -90, 90 ).type( DataTypes.DECIMAL_NUMBER ).check( data );
        DataTool.newDataChecker().pathRequired( LONGITUDE ).range( -180, 180 ).type( DataTypes.DECIMAL_NUMBER ).check( data );
    }
}
