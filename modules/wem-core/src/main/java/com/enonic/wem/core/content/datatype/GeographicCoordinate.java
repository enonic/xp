package com.enonic.wem.core.content.datatype;


import com.enonic.wem.core.content.type.configitem.fieldtype.FieldTypes;

public class GeographicCoordinate
    extends AbstractDataType
{
    GeographicCoordinate( int key )
    {
        super( key, JavaType.STRING, FieldTypes.GEO_LOCATION );
    }

    @Override
    public String getIndexableString( final Object value )
    {
        return null;
    }

    @Override
    public void checkValidity( final Object value )
        throws InvalidValueTypeException
    {
        super.checkValidity( value );

        String s = (String) value;
        // TODO: check s is on valid format, example: 40.446195, -79.948862
    }
}
